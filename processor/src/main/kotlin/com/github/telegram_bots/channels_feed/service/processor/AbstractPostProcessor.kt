package com.github.telegram_bots.channels_feed.service.processor

import com.github.telegram_bots.channels_feed.domain.Link
import com.github.telegram_bots.channels_feed.domain.PostInfo
import com.github.telegram_bots.channels_feed.domain.RawPost.PhotoContent
import com.github.telegram_bots.channels_feed.domain.RawPost.TextContent
import com.github.telegram_bots.channels_feed.domain.RawPost.TextContent.Entity
import com.github.telegram_bots.channels_feed.domain.RawPost.TextContent.Entity.Type
import com.github.telegram_bots.channels_feed.domain.RawPost.TextContent.Entity.Type.*
import com.github.telegram_bots.channels_feed.extension.UTF_16LE
import java.io.ByteArrayOutputStream

abstract class AbstractPostProcessor : PostProcessor {
    protected val MAX_MESSAGE_LENGTH: Int = 4096
    protected val MAX_CAPTION_LENGTH: Int = 200
    protected val SEPARATOR: String = "\n\n"

    protected fun extractFirstLink(info: PostInfo): Link {
        fun Type.isLink() = this == PLAIN_LINK || this == FORMATTED_LINK

        val content = info.first.content as? TextContent ?: return null
        return content
                .entities
                .find { it.type.isLink() }
                ?.let {
                    when (it.type) {
                        FORMATTED_LINK -> it.url
                        PLAIN_LINK -> it.extract(content.text.toByteArray(UTF_16LE))
                        else -> null
                    }
                }
                ?.let { """<a href="$it">\xad</a>""" }
    }

    protected fun extractFileId(info: PostInfo): String? {
        val content = info.first.content
        return when (content) {
            is PhotoContent -> content.photoId
            else -> null
        }
    }

    protected fun processText(info: PostInfo): String {
        val content = info.first.content
        return content.text
                .let {
                    if (content is TextContent)
                        it.replaceHTMLTags().convertEntities(content.entities)
                    else it
                }
    }

    private fun String.convertEntities(entities: List<Entity>): String {
        val source = toByteArray(UTF_16LE)
        return entities.asSequence()
                .map { Triple(it.format(source), it.startPos(), it.endPos()) }
                .fold(Triple(0, source, ByteArrayOutputStream()), {
                    (curPos, source, target), (replacement, startPos, endPos) ->
                            target.write(source.sliceArray(curPos until startPos))
                            target.write(replacement)
                            Triple(endPos, source, target)
                })
                .let { (endPos, source, target) ->
                    if (endPos < source.size) {
                        target.write(source.sliceArray(endPos until source.size))
                    }

                    target
                }
                .let { it.toByteArray().let { String(it, UTF_16LE) } }
    }

    private fun String.replaceHTMLTags() = replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("&", "&amp;")

    private fun Entity.format(bytes: ByteArray): ByteArray {
        val value = extract(bytes)
        val formatted = when (type) {
            FORMATTED_LINK -> """<a href="$url">$value</a>"""
            BOLD -> """<b>$value</b>"""
            ITALIC -> """<i>$value</i>"""
            CODE -> """<code>$value</code>"""
            PRE -> """"<pre>$value</pre>"""
            else -> value
        }

        return formatted.toByteArray(UTF_16LE)
    }

    private fun Entity.extract(bytes: ByteArray) = bytes.sliceArray(startPos() until endPos())
            .let { String(it, UTF_16LE) }

    private fun Entity.startPos() = offset * 2

    private fun Entity.endPos() = (length + offset) * 2
}

INSERT INTO Users VALUES (1, 1488);
INSERT INTO Users VALUES (2, 666);
INSERT INTO Channels VALUES (1, 'pi', 31415);
INSERT INTO Channels VALUES (2, 'e', 27182);
INSERT INTO Subscriptions VALUES (1, 1);
INSERT INTO Subscriptions VALUES (1, 2);
INSERT INTO Subscriptions VALUES (2, 2);

SELECT Users.id, Users.telegram_id 
FROM Users 
	JOIN Subscriptions ON Users.id=Subscriptions.user_id 
	JOIN Channels ON Subscriptions.channel_id=Channels.id 
WHERE Channels.name = 'pi';

SELECT Channels.id, Channels.name, Channels.telegram_id 
FROM Channels 
	JOIN Subscriptions ON Channels.id=Subscriptions.channel_id 
	JOIN Users ON Subscriptions.user_id=Users.id 
WHERE Users.telegram_id = 666;

SELECT Channels.id, Channels.name, Channels.telegram_id FROM Channels 
	JOIN Subscriptions ON Channels.id=Subscriptions.channel_id 
	JOIN Users ON Subscriptions.user_id=Users.id 
WHERE Users.telegram_id = 1488;

DELETE FROM Subscriptions USING Users 
WHERE Subscriptions.user_id = Users.id AND Users.telegram_id = 666;

INSERT INTO Subscriptions (user_id, channel_id, last_update) 
SELECT Users.id, Channels.id, now() 
	FROM Users, Channels 
	WHERE Users.telegram_id = 1488 AND Channels.telegram_id = 31415;

INSERT INTO Channels (id, name, telegram_id, last_update) 
SELECT 3, 'zero', 0, now() 
WHERE NOT EXISTS (
	SELECT 1 FROM Channels 
	WHERE telegram_id = 0 AND name = 'zero'
);

INSERT INTO Channels (id, name, telegram_id, last_update) SELECT 3, 'zero', 0, now() WHERE NOT EXISTS (SELECT 1 FROM Channels WHERE telegram_id = 0 AND name = 'zero');

DELETE FROM Subscriptions USING Channels, Users 
WHERE Subscriptions.channel_id = Channels.id AND Channels.name = 'e' AND Subscriptions.user_id = Users.id AND Users.telegram_id = '1488';

---------------------------------------
-- GET USER NAME AND STATS PROCEDURE --
---------------------------------------

CREATE PROCEDURE GetUserNameAndStats(
	@userId INT,
	@username VARCHAR(32) OUTPUT,
	@completed INT OUTPUT,
	@average INT OUTPUT,
	@perfect INT OUTPUT
)
AS

SELECT @username = Username
FROM [User]
WHERE ID = @userId

IF @username IS NULL
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END

SELECT @completed = SUM(Completed)
FROM GameCompletionByUser
WHERE UserID = @userId

SELECT @average = AVG((Completed * 100) / Total)
FROM GameCompletionByUser
WHERE UserID = @userId

SELECT @perfect = COUNT(GameID)
FROM GameCompletionByUser
WHERE UserID = @userId AND Completed = Total

RETURN 0
GO

SELECT * FROM [User]

----------------------------------
-- GET USER PLATFORMS PROCEDURE --
----------------------------------

CREATE PROCEDURE GetUserPlatforms(
	@userId INT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
SELECT [Platform].ID, [PlatformName], (CASE WHEN UserID IS NOT NULL THEN 1 ELSE 0 END) AS Connected
FROM [Platform]
LEFT JOIN IsOn ON IsOn.PlatformID = [Platform].ID AND UserID = @userId
ORDER BY [Platform].ID
RETURN 0
GO

--------------------------------
-- GET USER RATINGS PROCEDURE --
--------------------------------

CREATE PROCEDURE GetUserRatings(
	@userId INT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
SELECT Game.Name AS GameName, Achievement.Name AS AchievementName, Quality, Difficulty, Rating.[Description]
FROM Rating
JOIN Achievement ON Achievement.ID = Rating.AchievementID
JOIN Game ON Game.ID = Achievement.GameID
WHERE UserID = @userId
RETURN 0
GO

------------------------------
-- GET USER IMAGE PROCEDURE --
------------------------------

CREATE PROCEDURE GetUserImage(
	@userId INT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
SELECT ProfileImage FROM [User] WHERE ID = @userId
RETURN 0
GO

------------------
-- SET USERNAME --
------------------

CREATE PROCEDURE SetUsername(
	@userId INT,
	@username VARCHAR(32)
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
UPDATE [User] SET Username = @username WHERE ID = @userId
RETURN 0
GO

------------------------------
-- SET USER IMAGE PROCEDURE --
------------------------------

CREATE PROCEDURE SetUserImage(
	@userId INT,
	@type ImageType,
	@oldType ImageType OUTPUT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
SELECT @oldType = ProfileImage FROM [User] WHERE ID = @userId
UPDATE [User] SET ProfileImage = @type WHERE ID = @userId
RETURN 0
GO

--------------------------
-- ADD USER TO PLATFORM --
--------------------------

CREATE PROCEDURE AddUserToPlatform(
	@userId INT,
	@platformId INT,
	@platformUserID VARCHAR(32)
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
IF NOT @platformId IN (SELECT ID FROM [Platform])
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 2
END
IF EXISTS (SELECT * FROM IsOn WHERE UserID = @userId AND PlatformID = @platformId)
BEGIN
	PRINT 'User already exists on specified platform'
	RETURN 3
END
INSERT INTO IsOn VALUES (@userId, @platformId, @platformUserId)
RETURN 0
GO

-------------------------------
-- REMOVE USER FROM PLATFORM --
-------------------------------

CREATE PROCEDURE RemoveUserFromPlatform(
	@userId INT,
	@platformId INT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
IF NOT @platformId IN (SELECT ID FROM [Platform])
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 2
END
IF NOT EXISTS (SELECT UserID FROM IsOn WHERE UserID = @userId AND PlatformID = @platformId)
BEGIN
	PRINT 'User does not exist on specified platform'
	RETURN 3
END
DELETE FROM IsOn WHERE UserID = @userId AND PlatformID = @platformId
DELETE FROM Progress WHERE UserID = @userId AND PlatformID = @platformId
DELETE FROM Owns WHERE UserID = @userId AND PlatformID = @platformId
RETURN 0
GO

------------------
-- ADD PLATFORM --
------------------

CREATE PROCEDURE AddPlatform(
	@name VARCHAR(32),
	@platformId INT OUTPUT
)
AS
IF @name IS NULL
BEGIN
	PRINT 'Platform name cannot be null'
	RETURN 1
END
INSERT INTO [Platform] VALUES (@name)
SET @platformId = @@IDENTITY
RETURN 0
GO

---------------------
-- REMOVE PLATFORM --
---------------------

CREATE PROCEDURE RemovePlatform(
	@platformId INT
)
AS
IF NOT @platformId IN (SELECT ID FROM [Platform])
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 1
END
IF @platformId IN (SELECT PlatformID FROM ExistsOn)
BEGIN
	PRINT 'All games must be removed from the specified platform before it can be removed'
	RETURN 2
END
DELETE FROM [Platform] WHERE ID = @platformId
RETURN 0
GO

-------------------
-- GET PLATFORMS --
-------------------

CREATE PROCEDURE GetPlatforms
AS
SELECT ID, PlatformName FROM [Platform]
RETURN 0
GO

-----------------------
-- GET PLATFORM NAME --
-----------------------

CREATE PROCEDURE GetPlatformName(
	@platformId INT,
	@name VARCHAR(32) OUTPUT
)
AS
SELECT @name = PlatformName FROM [Platform] WHERE ID = @platformId
IF @name IS NULL
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 1
END
RETURN 0
GO

-----------------------
-- GET PLATFORM ICON --
-----------------------

CREATE PROCEDURE GetPlatformIcon(
	@platformId INT
)
AS
IF NOT @platformId IN (SELECT ID FROM [Platform])
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 1
END
SELECT Icon FROM [Platform] WHERE ID = @platformId
RETURN 0
GO

--------------
-- ADD GAME --
--------------

CREATE PROCEDURE AddGame(
	@name VARCHAR(32),
	@image ImageType,
	@gameId INT OUTPUT
)
AS
IF @name IS NULL
BEGIN
	PRINT 'Game name cannot be null'
	RETURN 1
END
IF @name IN (SELECT [Name] FROM Game)
BEGIN
	PRINT 'Game with specified name already exists'
	RETURN 2
END
INSERT INTO Game VALUES (@name, @image)
SET @gameId = @@IDENTITY
RETURN 0
GO

---------------------
-- ADD IF NOT GAME --
---------------------

CREATE PROCEDURE AddIfNotGame(
	@name VARCHAR(32),
	@image VARCHAR(11),
	@gameId INT OUTPUT
)
AS
IF @name IS NULL
BEGIN
	PRINT 'Game name cannot be null'
	RETURN 1
END
-- Ideally game name wouldn't have to be unique, but I don't know of another way to sync games across platforms when they share no IDing system
IF NOT @name IN (SELECT [Name] FROM Game)
BEGIN
	INSERT INTO Game VALUES (@name, @image)
END
SELECT @gameId = ID FROM Game WHERE [Name] = @name
RETURN 0
GO

-----------------
-- REMOVE GAME --
-----------------

CREATE PROCEDURE RemoveGame(
	@gameId INT
)
AS
IF NOT @gameId IN (SELECT ID FROM Game)
BEGIN
	PRINT 'No game with the specified ID was found'
	RETURN 1
END
DELETE FROM Game WHERE ID = @gameId
RETURN 0
GO

-------------------
-- GET GAME ICON --
-------------------

CREATE PROCEDURE GetGameIcon(
	@gameId INT
)
AS
IF NOT @gameId IN (SELECT ID FROM [Game])
BEGIN
	PRINT 'No game with the specified ID was found'
	RETURN 1
END
SELECT Icon FROM [Game] WHERE ID = @gameId
RETURN 0
GO

----------------------
-- ADD GAME TO USER --
----------------------

CREATE PROCEDURE AddGameToUser(
	@gameId INT,
	@userId INT,
	@platformId INT
)
AS
IF NOT @gameId IN (SELECT ID FROM Game)
BEGIN
	PRINT 'No game with the specified ID was found'
	RETURN 1
END
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 2
END
IF NOT @platformId IN (SELECT ID FROM [Platform])
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 3
END
IF NOT EXISTS (SELECT * FROM IsOn WHERE UserID = @userId AND PlatformID = @platformId)
BEGIN
	PRINT 'User is not on specified platform'
	RETURN 4
END
IF EXISTS (SELECT * FROM Owns WHERE GameID = @gameId AND UserID = @userId AND PlatformID = @platformId)
BEGIN
	PRINT 'Game is already owned by specified user on specified platform'
	RETURN 5
END
INSERT INTO Owns VALUES (@userId, @gameId, @platformId)
RETURN 0
GO

---------------------------
-- REMOVE GAME FROM USER --
---------------------------

CREATE PROCEDURE RemoveGameFromUser(
	@gameId INT,
	@userId INT,
	@platformId INT
)
AS
IF NOT @gameId IN (SELECT ID FROM Game)
BEGIN
	PRINT 'No game with the specified ID was found'
	RETURN 1
END
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 2
END
IF NOT @platformId IN (SELECT ID FROM [Platform])
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 3
END
IF NOT EXISTS (SELECT * FROM Owns WHERE GameID = @gameId AND UserID = @userId AND PlatformID = @platformId)
BEGIN
	PRINT 'Game is not owned by specified user on specified platform'
	RETURN 4
END
DELETE FROM Owns WHERE UserID = @userId AND GameID = @gameId AND PlatformID = @platformId
RETURN 0
GO

--------------------------
-- ADD GAME TO PLATFORM --
--------------------------

CREATE PROCEDURE AddGameToPlatform(
	@gameId INT,
	@platformId INT,
	@platformGameId VARCHAR(32)
)
AS
IF NOT @gameId IN (SELECT ID FROM Game)
BEGIN
	PRINT 'No game with the specified ID was found'
	RETURN 1
END
IF NOT @platformId IN (SELECT ID FROM [Platform])
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 2
END
IF EXISTS (SELECT * FROM ExistsOn WHERE GameID = @gameId AND PlatformID = @platformId)
BEGIN
	PRINT 'Game already exists on specified platform'
	RETURN 3
END
INSERT INTO ExistsOn VALUES (@gameId, @platformId, @platformGameId)
RETURN 0
GO

-------------------------------
-- REMOVE GAME FROM PLATFORM --
-------------------------------

CREATE PROCEDURE RemoveGameFromPlatform(
	@gameId INT,
	@platformId INT
)
AS
IF NOT @gameId IN (SELECT ID FROM Game)
BEGIN
	PRINT 'No game with the specified ID was found'
	RETURN 1
END
IF NOT @platformId IN (SELECT ID FROM [Platform])
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 2
END
IF NOT EXISTS (SELECT * FROM ExistsOn WHERE GameID = @gameId AND PlatformID = @platformId)
BEGIN
	PRINT 'Game does not exist on specified platform'
	RETURN 3
END
DELETE FROM ExistsOn WHERE GameID = @gameId AND PlatformID = @platformId
RETURN 0
GO

---------------------
-- ADD ACHIEVEMENT --
---------------------

CREATE PROCEDURE AddAchievement(
	@gameId INT,
	@name VARCHAR(128),
	@description VARCHAR(512),
	@stages INT,
	@image ImageType,
	@achievementId INT OUTPUT
)
AS
IF NOT @gameId IN (SELECT ID FROM Game)
BEGIN
	PRINT 'No game with the specified ID was found'
	RETURN 1
END
IF @name IS NULL
BEGIN
	PRINT 'Achievement name cannot be null'
	RETURN 2
END
IF @stages IS NULL
BEGIN
	PRINT 'Achievement stages cannot be null'
	RETURN 3
END
IF @name IN (SELECT [Name] FROM Achievement WHERE GameID = @gameId)
BEGIN
	PRINT 'Achievement with specified name already exists for specified game'
	RETURN 4
END
INSERT INTO Achievement VALUES (@gameId, @name, @description, @stages, @image)
SET @achievementId = @@IDENTITY
RETURN 0
GO

----------------------------
-- ADD IF NOT ACHIEVEMENT --
----------------------------

CREATE PROCEDURE AddIfNotAchievement(
	@gameId INT,
	@name VARCHAR(128),
	@description VARCHAR(512),
	@stages INT,
	@image VARCHAR(11),
	@achievementId INT OUTPUT
)
AS
IF NOT @gameId IN (SELECT ID FROM Game)
BEGIN
	PRINT 'No game with the specified ID was found'
	RETURN 1
END
IF @name IS NULL
BEGIN
	PRINT 'Achievement name cannot be null'
	RETURN 2
END
IF @stages IS NULL
BEGIN
	PRINT 'Achievement stages cannot be null'
	RETURN 3
END
IF NOT @name IN (SELECT [Name] FROM Achievement WHERE GameID = @gameId)
BEGIN
	INSERT INTO Achievement VALUES (@gameId, @name, @description, @stages, @image)
END
SELECT @achievementId = ID FROM Achievement WHERE [Name] = @name AND GameID = @gameId
RETURN 0
GO

------------------------
-- REMOVE ACHIEVEMENT --
------------------------

CREATE PROCEDURE RemoveAchievement(
	@achievementId INT
)
AS
IF NOT @achievementId IN (SELECT ID FROM Achievement)
BEGIN
	PRINT 'No achievement with the specified ID was found'
	RETURN 1
END
DELETE FROM Achievement WHERE ID = @achievementId
RETURN 0
GO

--------------------------
-- GET ACHIEVEMENT ICON --
--------------------------

CREATE PROCEDURE GetAchievementIcon(
	@achievementId INT
)
AS
IF NOT @achievementId IN (SELECT ID FROM Achievement)
BEGIN
	PRINT 'No achievement with the specified ID was found'
	RETURN 1
END
SELECT Icon FROM Achievement WHERE ID = @achievementId
RETURN 0
GO

---------------------------------------
-- SET ACHIEVEMENT PROGRESS FOR USER --
---------------------------------------

CREATE PROCEDURE SetAchievementProgressForUser(
	@userId INT,
	@platformId INT,
	@achievementId INT,
	@progress INT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
IF NOT @platformId IN (SELECT ID FROM [Platform])
BEGIN
	PRINT 'No platform with the specified ID was found'
	RETURN 2
END
IF NOT @achievementId IN (SELECT ID FROM Achievement)
BEGIN
	PRINT 'No achievement with the specified ID was found'
	RETURN 3
END
IF EXISTS (SELECT * FROM Progress WHERE AchievementID = @achievementId AND UserID = @userId AND PlatformID = @platformId)
BEGIN
	UPDATE Progress SET Progress = @progress WHERE AchievementID = @achievementId AND UserID = @userId AND PlatformID = @platformId
END
ELSE
BEGIN
	INSERT INTO Progress VALUES (@userId, @platformId, @achievementId, @progress)
END
RETURN 0
GO


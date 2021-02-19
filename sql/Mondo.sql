    ---------------------
    ---------------------
    --                 --
------  CREATE TABLES  ------
    --                 --
    ---------------------
    ---------------------

CREATE TYPE ImageType FROM VARCHAR(4) NULL
GO

CREATE TABLE [User] (
	ID INT IDENTITY(0, 1) NOT NULL,
	Email VARCHAR(254) NOT NULL,
	Username VARCHAR(32) NOT NULL,
	[Password] CHAR(64) NOT NULL,
	[Salt] CHAR(32) NOT NULL,
	Hue INT NOT NULL
		CONSTRAINT HueDefault DEFAULT 0
		CONSTRAINT HueConstraint CHECK (0 <= Hue AND Hue <= 360),
	ProfileImage ImageType,
	[Admin] BIT NOT NULL
		CONSTRAINT AdmivDefault DEFAULT 0,
	Verified BIT NOT NULL
		CONSTRAINT VerifiedDefault DEFAULT 0
	PRIMARY KEY(ID)
)

CREATE TABLE [Platform] (
	ID INT IDENTITY(0, 1) NOT NULL,
	PlatformName VARCHAR(32) NOT NULL,
	Icon ImageType
	PRIMARY KEY(ID)
)

CREATE TABLE [Game] (
	ID INT IDENTITY(0, 1) NOT NULL,
	Name VARCHAR(32) NOT NULL,
	Icon ImageType
	PRIMARY KEY(ID)
)

CREATE TABLE [Achievement] (
	ID INT IDENTITY(0, 1) NOT NULL,
	GameID INT NOT NULL,
	Name VARCHAR(128) NOT NULL,
	Description VARCHAR(512) NULL,
	Stages INT NOT NULL,
	Icon ImageType
	PRIMARY KEY(ID)
	FOREIGN KEY(GameID) REFERENCES [Game](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)

CREATE TABLE [Owns] (
	UserID INT NOT NULL,
	GameID INT NOT NULL,
	PlatformID INT NOT NULL
	PRIMARY KEY(UserID, GameID, PlatformID)
	FOREIGN KEY(UserID) REFERENCES [User](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY(GameID) REFERENCES [Game](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY(PlatformID) REFERENCES [Platform](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)

CREATE TABLE [Progress] (
	UserID INT NOT NULL,
	PlatformID INT NOT NULL,
	AchievementID INT NOT NULL,
	Progress INT NOT NULL
	PRIMARY KEY(UserID, PlatformID, AchievementID)
	FOREIGN KEY(UserID) REFERENCES [User](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY(PlatformID) REFERENCES [Platform](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY(AchievementID) REFERENCES [Achievement](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)

CREATE TABLE [IsOn] (
	UserID INT NOT NULL,
	PlatformID INT NOT NULL,
	PlatformUserID VARCHAR(32) NOT NULL
	PRIMARY KEY(UserID, PlatformID)
	FOREIGN KEY(UserID) REFERENCES [User](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY(PlatformID) REFERENCES [Platform](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)

CREATE TABLE [ExistsOn] (
	GameID INT NOT NULL,
	PlatformID INT NOT NULL,
	PlatformGameID VARCHAR(32) NOT NULL
	PRIMARY KEY(GameID, PlatformID)
	FOREIGN KEY(GameID) REFERENCES [Game](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY(PlatformID) REFERENCES [Platform](ID)
		ON UPDATE CASCADE
)

CREATE TABLE [Rating] (
	UserID INT NOT NULL,
	AchievementID INT NOT NULL,
	Quality FLOAT NULL,
	Difficulty FLOAT NULL,
	[Description] VARCHAR(1024) NULL
	PRIMARY KEY(UserID, AchievementID)
	FOREIGN KEY(UserID) REFERENCES [User](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY(AchievementID) REFERENCES [Achievement](ID)
		ON UPDATE CASCADE
		ON DELETE CASCADE
)

    --------------------
    --------------------
    --                --
------  CREATE VIEWS  ------
    --                --
    --------------------
    --------------------

-- The maximum progress a user has on an achievement across all platforms
CREATE VIEW MaxProgress
AS
	SELECT UserID, AchievementID, MAX(Progress) AS Progress
	FROM Progress
	GROUP BY UserID, AchievementID
GO

-- List of games and users with the number of completed achievements out of the total achievements the user has completed
CREATE VIEW GameCompletionByUser
AS
	SELECT UserID, GameID, SUM(CASE WHEN Progress = Stages THEN 1 ELSE 0 END) AS Completed, COUNT(AchievementID) AS Total
	FROM Achievement
	JOIN MaxProgress ON AchievementID = Achievement.ID
	GROUP BY UserID, GameID
GO

-- List of achievements and the percentage of people who have completed it
CREATE VIEW AchievementCompletion
AS
	SELECT Achievement.ID, (CASE WHEN COUNT(UserID) = 0 THEN NULL ELSE (SUM(CASE WHEN Progress = Stages THEN 1 ELSE 0 END) * 100 / COUNT(UserID)) END) AS Completion, COUNT(UserID) AS NumberUsers
	FROM Achievement
	LEFT JOIN MaxProgress ON AchievementID = Achievement.ID
	GROUP BY Achievement.ID
GO

-- List of achievements and their average quality and difficulty ratings filling with null as necessary
CREATE VIEW AchievementRatings
AS
	SELECT Achievement.ID, AVG(Quality) AS Quality, AVG(Difficulty) AS Difficulty
	FROM Achievement
	LEFT JOIN Rating ON AchievementID = Achievement.ID
	GROUP BY Achievement.ID
GO

-- List of games owned by a user removing duplicate ownership if owned on multiple platforms
CREATE VIEW OwnsUnique
AS
	SELECT UserID, GameID
	FROM Owns
	GROUP BY UserID, GameID
GO

    ----------------------
    ----------------------
    --                  --
------  CREATE INDICES  ------
    --                  --
    ----------------------
    ----------------------

-----------------
-- Email Index --
-----------------

CREATE NONCLUSTERED INDEX EmailIndex ON [User](Email)

------------------
-- Name Indexes --
------------------

CREATE NONCLUSTERED INDEX UsernameIndex ON [User](Username)
CREATE NONCLUSTERED INDEX GameNameIndex ON Game(Name)
CREATE NONCLUSTERED INDEX AchievementNameIndex ON Achievement(Name)

--------------------
-- Rating Indexes --
--------------------

CREATE NONCLUSTERED INDEX DifficultyIndex ON Rating(Difficulty)
CREATE NONCLUSTERED INDEX QualityIndex ON Rating(Quality)

    -------------------------
    -------------------------
    --                     --
------  CREATE PROCEDURES  ------
    --                     --
    -------------------------
    -------------------------

-----------------------
-- CREATE A NEW USER --
-----------------------

CREATE PROCEDURE [CreateUser] (
	@email VARCHAR(254),
	@username VARCHAR(32),
	@salt CHAR(32),
	@password CHAR(64),
	@ID INT OUTPUT,
	@Hue INT OUTPUT
) AS
IF EXISTS (SELECT Email FROM [User] WHERE Email = @email)
BEGIN
	PRINT 'Email is already registered with an account.'
	RETURN 1
END
IF @username IS NULL
BEGIN
	PRINT 'Username cannot be null'
	RETURN 2
END
IF @password IS NULL
BEGIN
	PRINT 'Password cannot be null'
	RETURN 3
END
IF @salt IS NULL
BEGIN
	PRINT 'Password salt cannot be null'
	RETURN 4
END

INSERT INTO [User](Email, Username, Salt, [Password]) VALUES (@email, @username, @salt, @password)
SET @ID = @@IDENTITY
SELECT @Hue = Hue FROM [User] WHERE ID = @ID

RETURN 0
GO

-------------------------
-- GET USER LOGIN INFO --
-------------------------

CREATE PROCEDURE GetUserLogin(
	@email VARCHAR(254)
) AS
IF NOT @email IN (SELECT Email FROM [User])
BEGIN
	PRINT 'No user exists with specified email'
	RETURN 1
END
SELECT Id, Salt, [Password], Hue, [Admin] FROM [User] WHERE Email = @email
RETURN 0
GO

DELETE FROM [User]
DELETE FROM [Game]
DELETE FROM [Platform]
GO

--------------
-- HAS USER --
--------------

CREATE PROCEDURE HasUser(
	@result BIT OUTPUT
)
AS
SET @result = CASE WHEN EXISTS (SELECT * FROM [User]) THEN 1 ELSE 0 END
RETURN 0
GO

-------------
-- OP USER --
-------------

CREATE PROCEDURE OpUser(
	@userId INT
)
AS
UPDATE [User] SET Admin = 1 WHERE @userId = [User].ID
RETURN 0
GO

-----------------------
-- GET ID FROM EMAIL --
-----------------------

CREATE PROCEDURE GetIdFromEmail(
	@email VARCHAR(254),
	@userId INT OUTPUT
)
AS
SELECT @userId = ID
FROM [User]
WHERE Email = @email
RETURN 0
GO

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
INSERT INTO [Platform] VALUES (@name, 'png')
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

------------------------------------------
-- GET NOTEWORTHY ACHIEVEMENTS FOR USER --
------------------------------------------

CREATE PROCEDURE GetNoteworthyAchievementsForUser (
	@userId INT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
SELECT TOP 5 Achievement.ID, Name, Completion	
FROM Achievement
JOIN MaxProgress ON Achievement.ID = MaxProgress.AchievementID
JOIN AchievementCompletion AC ON AC.ID = Achievement.ID
WHERE UserID = @userId AND Progress = Stages
ORDER BY Completion ASC, NumberUsers DESC
RETURN 0
GO

---------------------
-- GET ACHIEVEMENT --
---------------------

CREATE PROCEDURE GetAchievement (
	@achievementId INT
)
AS
IF NOT @achievementId IN (SELECT ID FROM Achievement)
BEGIN
	PRINT 'No achievement with the specified ID was found'
	RETURN 1
END
SELECT Achievement.ID, Name, Completion, Description, Difficulty, Quality
FROM Achievement
LEFT JOIN AchievementCompletion AC ON Achievement.ID = AC.ID
LEFT JOIN AchievementRatings AR ON Achievement.ID = AR.ID
WHERE Achievement.ID = @achievementId
RETURN 0
GO

---------------------------------
-- GET RATINGS FOR ACHIEVEMENT --
---------------------------------

CREATE PROCEDURE GetRatingsForAchievement(
	@achievementId INT
)
AS
IF NOT @achievementId IN (SELECT ID FROM Achievement)
BEGIN
	PRINT 'No achievement with the specified ID was found'
	RETURN 1
END
SELECT UserID, [Username], Difficulty, Quality, [Description]
FROM Rating
JOIN [User] ON [User].ID = Rating.UserID
WHERE AchievementID = @achievementId
RETURN 0
GO

-------------------------
-- GET RATINGS BY USER --
-------------------------

CREATE PROCEDURE GetRatingsByUser(
	@userId INT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
SELECT AchievementID, Achievement.[Name], Difficulty, Quality, Rating.[Description]
FROM Rating
JOIN Achievement ON Achievement.ID = Rating.UserID
WHERE UserID = @userId
RETURN 0
GO

EXEC GetRatingsByUser 0

------------------
-- HAS PROGRESS --
------------------

CREATE PROCEDURE HasProgress (
	@userId INT,
	@achievementId INT,
	@result BIT OUTPUT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
IF NOT @achievementId IN (SELECT ID FROM Achievement)
BEGIN
	PRINT 'No achievement with the specified ID was found'
	RETURN 2
END
SET @result = CASE WHEN EXISTS (SELECT * FROM Progress WHERE UserID = @userId AND AchievementID = @achievementId) THEN 1 ELSE 0 END
RETURN 0
GO

----------------
-- GET RATING --
----------------

CREATE PROCEDURE GetRating(
	@userId INT,
	@achievementId INT
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
IF NOT @achievementId IN (SELECT ID FROM Achievement)
BEGIN
	PRINT 'No achievement with the specified ID was found'
	RETURN 2
END
SELECT Difficulty, Quality, [Description]
FROM Rating
WHERE UserID = @userId AND AchievementID = @achievementId
RETURN 0
GO

----------------
-- SET RATING --
----------------

CREATE PROCEDURE SetRating(
	@userId INT,
	@achievementId INT,
	@difficulty FLOAT,
	@quality FLOAT,
	@review VARCHAR(1024)
)
AS
IF NOT @userId IN (SELECT ID FROM [User])
BEGIN
	PRINT 'No user with the specified ID was found'
	RETURN 1
END
IF NOT @achievementId IN (SELECT ID FROM Achievement)
BEGIN
	PRINT 'No achievement with the specified ID was found'
	RETURN 2
END
IF NOT EXISTS (SELECT * FROM Progress WHERE UserID = @userId AND AchievementID = @achievementId)
BEGIN
	PRINT 'User does not have progress on achievement'
	RETURN 3
END
IF @difficulty < 0 OR @difficulty > 10
BEGIN
	PRINT 'Difficult must be between 0 and 10'
	RETURN 4
END
IF @quality < 0 OR @quality > 10
BEGIN
	PRINT 'Quality must be between 0 and 10'
	RETURN 5
END
IF @quality IS NULL AND @quality IS NULL AND @review IS NULL
	DELETE FROM Rating WHERE UserID = @userId AND AchievementID = @achievementId
ELSE IF EXISTS (SELECT * FROM Rating WHERE UserID = @userId AND AchievementID = @achievementId)
	UPDATE Rating SET
		Quality = @quality,
		Difficulty = @difficulty,
		[Description] = @review
	WHERE UserID = @userId AND AchievementID = @achievementId
ELSE
	INSERT INTO Rating VALUES (@userId, @achievementId, @quality, @difficulty, @review)
RETURN 0
GO

-------------------------
-- SEARCH ACHIEVEMENTS --
-------------------------

CREATE PROCEDURE SearchAchievements(
	@searchTerm     VARCHAR(32),
	@userId         INT,
	@completed      BIT,
	@minCompletion  FLOAT,
	@maxCompletion  FLOAT,
	@minDifficulty  FLOAT,
	@maxDifficulty  FLOAT,
	@minQuality     FLOAT,
	@maxQuality     FLOAT,
	@orderBy        VARCHAR(16),
	@orderDirection VARCHAR(4)
)
AS
IF @userId IS NULL AND @completed = 1
BEGIN
	PRINT 'Cannot search for completed achievements with no user specified'
	RETURN 1
END

IF @completed IS NULL
SET @completed = 0

IF @searchTerm IS NULL OR @searchTerm = ''
	SET @searchTerm = '%'
ELSE
	SET @searchTerm = '%' + @searchTerm + '%'

SELECT TOP 100 Achievement.ID, Game.[Name] AS Game, Achievement.[Name], Completion, Difficulty, Quality
FROM Achievement
JOIN Game ON Game.ID = GameID
JOIN AchievementCompletion AC ON AC.ID = Achievement.ID
JOIN AchievementRatings AR ON AR.ID = Achievement.ID
WHERE (Game.[Name] LIKE @searchTerm OR Achievement.[Name] LIKE @searchTerm)
	AND (@completed     <> 1    OR Achievement.ID IN (SELECT AchievementID FROM MaxProgress WHERE UserID = @userId AND Progress = Stages))
	AND (@minCompletion IS NULL OR @minCompletion <= Completion)
	AND (@maxCompletion IS NULL OR @maxCompletion >= Completion)
	AND (@minDifficulty IS NULL OR @minDifficulty <= Difficulty)
	AND (@maxDifficulty IS NULL OR @maxDifficulty >= Difficulty)
	AND (@minQuality    IS NULL OR @minQuality    <= Quality   )
	AND (@maxQuality    IS NULL OR @maxQuality    >= Quality   )
ORDER BY
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'Game'       THEN Game.[Name]        ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'Name'       THEN Achievement.[Name] ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'Completion' THEN Completion         ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'Difficulty' THEN Difficulty         ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'Quality'    THEN Quality            ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'Game'       THEN Game.[Name]        ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'Name'       THEN Achievement.[Name] ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'Completion' THEN Completion         ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'Difficulty' THEN Difficulty         ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'Quality'    THEN Quality            ELSE NULL END DESC
RETURN 0
GO

------------------
-- SEARCH USERS --
------------------

CREATE PROCEDURE SearchUsers(
	@searchTerm       VARCHAR(32),
	@minOwned         INT,
	@maxOwned         INT,
	@minCompleted     INT,
	@maxCompleted     INT,
	@minAvgCompletion INT,
	@maxAvgCompletion INT,
	@orderBy          VARCHAR(16),
	@orderDirection   VARCHAR(4)
)
AS

IF @searchTerm IS NULL OR @searchTerm = ''
	SET @searchTerm = '%'
ELSE
	SET @searchTerm = '%' + @searchTerm + '%'

SELECT TOP 100 *
FROM (
	SELECT [User].ID, Username, ISNULL(GameCount, 0) AS GameCount, ISNULL(AchievementCount, 0) AS AchievementCount, AvgCompletion, ISNULL(PerfectGames, 0) AS PerfectGames
	FROM [User]
	LEFT JOIN (
		SELECT
			UserID,
			COUNT(GameID) AS GameCount,
			SUM(Completed) AS AchievementCount,
			AVG((Completed * 100) / Total) AS AvgCompletion,
			SUM(CASE WHEN Completed = Total THEN 1 ELSE 0 END) AS PerfectGames
		FROM GameCompletionByUser
		GROUP BY UserID
	) AS Completion ON Completion.UserID = [User].ID
) AS Results
WHERE (Username LIKE @searchTerm)
	AND (@minOwned         IS NULL OR @minOwned         <= GameCount       )
	AND (@maxOwned         IS NULL OR @maxOwned         >= GameCount       )
	AND (@minCompleted     IS NULL OR @minCompleted     <= AchievementCount)
	AND (@maxCompleted     IS NULL OR @maxCompleted     >= AchievementCount)
	AND (@minAvgCompletion IS NULL OR @minAvgCompletion <= AvgCompletion   )
	AND (@maxAvgCompletion IS NULL OR @maxAvgCompletion >= AvgCompletion   )
ORDER BY
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'Username'         THEN Username         ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'GameCount'        THEN GameCount        ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'AchievementCount' THEN AchievementCount ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'AvgCompletion'    THEN AvgCompletion    ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'PerfectCount'     THEN PerfectGames     ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'Username'         THEN Username         ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'GameCount'        THEN GameCount        ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'AchievementCount' THEN AchievementCount ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'AvgCompletion'    THEN AvgCompletion    ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'PerfectCount'     THEN PerfectGames     ELSE NULL END DESC
RETURN 0
GO
------------------
-- SEARCH GAMES --
------------------

CREATE PROCEDURE SearchGames(
	@searchTerm       VARCHAR(32),
	@userId           INT,
	@owned            BIT,
	@minAvgCompletion INT,
	@maxAvgCompletion INT,
	@minNumOwners     INT,
	@maxNumOwners     INT,
	@minNumPerfects   INT,
	@maxNumPerfects   INT,
	@orderBy          VARCHAR(16),
	@orderDirection   VARCHAR(4)
)
AS
IF @userId IS NULL AND @owned = 1
BEGIN
	PRINT 'Cannot search for owned games with no user specified'
	RETURN 1
END

PRINT 'UserID, Owned'
PRINT @userId
PRINT @owned

IF @owned IS NULL
SET @owned = 0

IF @searchTerm IS NULL OR @searchTerm = ''
	SET @searchTerm = '%'
ELSE
	SET @searchTerm = '%' + @searchTerm + '%'

SELECT TOP 100 *
FROM (
	SELECT
		Game.ID,
		[Name],
		AchievementCount,
		AvgCompletion,
		ISNULL(NumOwners, 0) AS NumOwners,
		ISNULL(NumPerfects, 0) AS NumPerfects
	FROM Game
	LEFT JOIN (
		SELECT
			GameID,
			Total AS AchievementCount,
			AVG((Completed * 100) / Total) AS AvgCompletion,
			SUM(CASE WHEN Completed = Total THEN 1 ELSE 0 END) AS NumPerfects
		FROM GameCompletionByUser
		GROUP BY GameID, Total
	) AS Completion ON Completion.GameID = Game.ID
	LEFT JOIN (
		SELECT GameID, COUNT(UserID) AS NumOwners
		FROM OwnsUnique
		GROUP BY GameID
	) AS Owners ON Owners.GameID = Game.ID
) AS Results
WHERE ([Name] LIKE @searchTerm)
	AND (@owned            <> 1    OR ID IN (SELECT GameID FROM OwnsUnique WHERE UserID = @userId))
	AND (@minAvgCompletion IS NULL OR @minAvgCompletion <= AvgCompletion)
	AND (@maxAvgCompletion IS NULL OR @maxAvgCompletion >= AvgCompletion)
	AND (@minNumOwners     IS NULL OR @minNumOwners     <= NumOwners    )
	AND (@maxNumOwners     IS NULL OR @maxNumOwners     >= NumOwners    )
	AND (@minNumPerfects   IS NULL OR @minNumPerfects   <= NumPerfects  )
	AND (@maxNumPerfects   IS NULL OR @maxNumPerfects   >= NumPerfects  )
ORDER BY
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'Name'             THEN [Name]           ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'AchievementCount' THEN AchievementCount ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'AvgCompletion'    THEN AvgCompletion    ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'NumOwners'        THEN NumOwners        ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'ASC'  AND @orderBy = 'NumPerfects'      THEN NumPerfects      ELSE NULL END ASC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'Name'             THEN [Name]           ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'AchievementCount' THEN AchievementCount ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'AvgCompletion'    THEN AvgCompletion    ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'NumOwners'        THEN NumOwners        ELSE NULL END DESC,
	CASE WHEN @orderDirection = 'DESC' AND @orderBy = 'NumPerfects'      THEN NumPerfects      ELSE NULL END DESC
RETURN 0
GO

EXEC SearchGames '', 3, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL

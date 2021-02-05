------ DROP ALL TABLES ------
--
-- Courtesy of: https://stackoverflow.com/questions/8439650/how-to-drop-all-tables-in-a-sql-server-database
--
-----------------------------

--DECLARE @Sql NVARCHAR(500) DECLARE @Cursor CURSOR

--SET @Cursor = CURSOR FAST_FORWARD FOR
--SELECT DISTINCT sql = 'ALTER TABLE [' + tc2.TABLE_SCHEMA + '].[' +  tc2.TABLE_NAME + '] DROP [' + rc1.CONSTRAINT_NAME + '];'
--FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc1
--LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc2 ON tc2.CONSTRAINT_NAME =rc1.CONSTRAINT_NAME

--OPEN @Cursor FETCH NEXT FROM @Cursor INTO @Sql

--WHILE (@@FETCH_STATUS = 0)
--BEGIN
--Exec sp_executesql @Sql
--FETCH NEXT FROM @Cursor INTO @Sql
--END

--CLOSE @Cursor DEALLOCATE @Cursor
--GO

--EXEC sp_MSforeachtable 'DROP TABLE ?'
--GO

-----------------------------

CREATE TABLE [User] (
	ID INT IDENTITY(0, 1) NOT NULL,
	Email VARCHAR(254) NOT NULL,
	Username VARCHAR(32) NOT NULL,
	[Password] CHAR(64) NOT NULL,
	[Salt] CHAR(32) NOT NULL
	PRIMARY KEY(ID)
)

CREATE TABLE [Platform] (
	ID INT IDENTITY(0, 1) NOT NULL,
	PlatformName VARCHAR(32) NOT NULL
	PRIMARY KEY(ID)
)

CREATE TABLE [Game] (
	ID INT IDENTITY(0, 1) NOT NULL,
	Name VARCHAR(32) NOT NULL,
	Thumbnail VARCHAR(256) NULL
	PRIMARY KEY(ID)
)

CREATE TABLE [Achievement] (
	ID INT IDENTITY(0, 1) NOT NULL,
	GameID INT NOT NULL,
	Name VARCHAR(128) NOT NULL,
	Description VARCHAR(512) NULL,
	Stages INT NOT NULL,
	Thumbnail VARCHAR(256) NULL
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
	PRIMARY KEY(UserID, AchievementID)
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
	PlatformID INT NOT NULL
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
	PlatformGameID INT NOT NULL
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

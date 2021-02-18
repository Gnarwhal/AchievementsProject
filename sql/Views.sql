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
	SELECT Achievement.ID, (CASE WHEN COUNT(UserID) = 0 THEN NULL ELSE (SUM(CASE WHEN Progress = Stages THEN 1 ELSE 0 END) * 100 / COUNT(UserID)) END) AS Completion
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

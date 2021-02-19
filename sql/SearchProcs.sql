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

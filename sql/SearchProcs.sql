-----------------------
-- SEARCH ACHIEVEMENTS --
-----------------------

CREATE PROCEDURE SearchAchievements(
	@searchTerm VARCHAR(32),
	@userId INT,
	@completed BIT,
	@minCompletion FLOAT,
	@maxCompletion FLOAT,
	@minDifficulty FLOAT,
	@maxDifficulty FLOAT,
	@minQuality FLOAT,
	@maxQuality FLOAT
)
AS
IF @userId IS NULL AND @completed = 1
BEGIN
	PRINT 'Cannot search for completed achievements with no user specified'
	RETURN 1
END

IF @searchTerm IS NULL OR @searchTerm = ''
	SET @searchTerm = '%'
ELSE
	SET @searchTerm = '%' + @searchTerm + '%'
PRINT @searchTerm

IF NOT @userId IS NULL
	SELECT TOP 100 Game.[Name] AS Game, Achievement.[Name], Completion, Difficulty, Quality
	FROM Achievement
	JOIN MaxProgress ON AchievementID = Achievement.ID AND UserID = @userId
	JOIN Game ON Game.ID = GameID
	JOIN AchievementCompletion AC ON AC.ID = Achievement.ID
	JOIN AchievementRatings AR ON AR.ID = Achievement.ID
	WHERE (Game.[Name] LIKE @searchTerm OR Achievement.[Name] LIKE @searchTerm)
		AND (@completed     <> 1    OR Progress        = Stages    )
		AND (@minCompletion IS NULL OR @minCompletion <= Completion)
		AND (@maxCompletion IS NULL OR @maxCompletion >= Completion)
		AND (@minDifficulty IS NULL OR @minDifficulty <= Difficulty)
		AND (@maxDifficulty IS NULL OR @maxDifficulty >= Difficulty)
		AND (@minQuality    IS NULL OR @minQuality    <= Quality   )
		AND (@maxQuality    IS NULL OR @maxQuality    >= Quality   )
ELSE
	SELECT TOP 100 Achievement.ID, Game.[Name] AS Game, Achievement.[Name], Completion, Quality, Difficulty
	FROM Achievement
	JOIN Game ON Game.ID = GameID
	JOIN AchievementCompletion AC ON AC.ID = Achievement.ID
	JOIN AchievementRatings AR ON AR.ID = Achievement.ID
	WHERE (Game.[Name] LIKE @searchTerm OR Achievement.[Name] LIKE @searchTerm)
		AND (@minCompletion IS NULL OR @minCompletion <= Completion)
		AND (@maxCompletion IS NULL OR @maxCompletion >= Completion)
		AND (@minDifficulty IS NULL OR @minDifficulty <= Difficulty)
		AND (@maxDifficulty IS NULL OR @maxDifficulty >= Difficulty)
		AND (@minQuality    IS NULL OR @minQuality    <= Quality   )
		AND (@maxQuality    IS NULL OR @maxQuality    >= Quality   )
RETURN 0
GO

EXEC SearchAchievements '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
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


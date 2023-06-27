DROP DATABASE IF EXISTS `VIDEO_PLATFORM`;
CREATE DATABASE IF NOT EXISTS `VIDEO_PLATFORM`;
USE `VIDEO_PLATFORM`;

-- VIDEO_PLATFORM.MANAGER TABLE 내보내기
DROP TABLE IF EXISTS `MANAGER`;
CREATE TABLE IF NOT EXISTS `MANAGER` (
`managerID` VARCHAR(10) NOT NULL,
`Fname` varchar(20) NOT NULL,
`Lname` varchar(20) NOT NULL,
`managerPW` varchar(10) NOT NULL,
PRIMARY KEY (`managerID`)
) ENGINE=InnoDB;

-- VIDEO_PLATFORM.MANAGER TABLE의 데이터 내보내기
DELETE FROM `MANAGER`;
INSERT INTO `MANAGER` (`managerID`, `Fname`, `Lname`, `managerPW`) VALUES
	("manager", "Jane", "Gonzalez", "aoslwj"),
    ("manager1", "Freda", "Galloway", "aoslwj1"),
    ("manager2", "James", "Becker", "aoslwj2"),
    ("manager3", "John", "Fay", "aoslwj3"),
    ("manager4", "Randell", "Rountree", "aoslwj4"),
    ("manager5", "Gloira", "Parks", "aoslwj5");

-- VIDEO_PLATFORM.USER TABLE 내보내기
DROP TABLE IF EXISTS `USER`;
CREATE TABLE IF NOT EXISTS `USER`(
`managerID` varchar(10) NOT NULL DEFAULT "manager",
`userID` varchar(10) NOT NULL,
`userPW` varchar(10) NOT NULL,
`subscribedNum` int NOT NULL DEFAULT 0,
`userReportNum` int NOT NULL DEFAULT 0,
`age` int NOT NULL,
PRIMARY KEY (`userID`),
FOREIGN KEY(`managerID`) REFERENCES `MANAGER` (`managerID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- VIDEO_PLATFORM.USER TABLE의 데이터 내보내기
DELETE FROM `USER`;
INSERT INTO `USER` (`managerID`, `userID`, `userPW`, `subscribedNum`, `userReportNum`, `age`) VALUES
	("manager1", "user1", "dbwj1", 2, 100, "23"),
    ("manager1", "2021025205", "ktw020409", 2, 0, "10"),
    ("manager2", "user2", "dbwj2", 1, 99, "44"),
    ("manager2", "youtube", "dbxbqm", 1, 0, "12"),
    ("manager3", "user3", "dbwj3", 0, 0, "19"),
    ("manager3", "hyu", "gksdideo", 1, 0, "13"),
    ("manager4", "user4", "dbwj4", 1, 0, "22");

-- VIDEO_PLATFORM.SUBSCRIBES TABLE 내보내기
DROP TABLE IF EXISTS `SUBSCRIBES`;
CREATE TABLE IF NOT EXISTS `SUBSCRIBES` (
`userID` varchar(10) NOT NULL,
`subUserID` varchar(10) NOT NULL,
PRIMARY KEY (`userID`, `subUserID`),
FOREIGN KEY (`userID`) REFERENCES USER(`userID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- VIDEO_PLATFORM.SUBSRCIBES TABLE의 데이터 내보내기
DELETE FROM `SUBSCRIBES`;
INSERT INTO `SUBSCRIBES` (`userID`, `subUserID`) VALUES
	("user1", "2021025205"), ("user1", "hyu"),
    ("youtube", "user1"),
    ("hyu", "user1"), ("hyu", "2021025205"), ("hyu", "user2"), ("hyu", "youtube"), ("hyu", "user4");

-- VIDEO_PLATFORM.VIDEO TABLE 내보내기
DROP TABLE IF EXISTS `VIDEO`;
CREATE TABLE IF NOT EXISTS `VIDEO` (
`uploaderID` varchar(10) NOT NULL,
`videoID` int(8) NOT NULL AUTO_INCREMENT,
`videoTitle` varchar(100) NOT NULL, 
`uploadDate` date NOT NULL,
`videoLength` time NOT NULL,
`views` int NOT NULL DEFAULT 0,
`likes` int NOT NULL DEFAULT 0,
`vReportNum` int NOT NULL DEFAULT 0,
`ageLimit` int NOT NULL DEFAULT 0,
PRIMARY KEY (`videoID`),
UNIQUE (`uploaderID`, `videoTitle`),
FOREIGN KEY (`uploaderID`) REFERENCES `USER` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- VIDEO_PLATFORM.USER_VIDEO TABLE의 데이터 내보내기
DELETE FROM `VIDEO`;
INSERT INTO `VIDEO` (`uploaderID`, `videoTitle`, `uploadDate`, `videoLength`, `views`, `likes`, `vReportNum`, `ageLimit`) VALUES
	("user1",  "qwerty", "2008-02-04", "00:05:05", 10, 0, 100, 19),
    ("user2", "Hello", "2009-06-09", "00:00:08", 11, 0, 0, 0),
    ("user1", "video1", "2022-11-30", "00:13:12", 20, 0, 0, 15),
    ("user1", "qooo", "2022-11-30", "00:14:05", 1, 0, 0, 0),
    ("user1", "Hello", "2012-11-05", "00:00:54", 3, 0, 0, 0);
    
-- VIDEO_PLATFORM.WATCH TABLE 내보내기
DROP TABLE IF EXISTS `WATCH`;
CREATE TABLE IF NOT EXISTS `WATCH` (
`userID` varchar(10) NOT NULL,
`watchVideoID` INT NOT NULL,
`watchStartTime` timestamp NOT NULL,
`comment` varchar(500) DEFAULT NULL,
PRIMARY KEY (`userID`, `watchVideoID`, `watchStartTime`),
FOREIGN KEY (`userID`) REFERENCES `USER` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`watchVideoID`) REFERENCES `VIDEO` (`videoID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- VIDEO_PLATFORM.WATCH TABLE의 데이터 내보내기
DELETE FROM `WATCH`;
INSERT INTO `WATCH` (`userID`, `watchVideoID`, `watchStartTime`, `comment`) VALUES
	("user1", 2, '2022-11-30 15:50:47', NULL),
	("user1", 2, '2014-06-16 14:35:57', "wowowowow"),
    ("user4", 1, '2014-06-16 14:35:57', "gooooood"),
    ("user2", 1, '2022-11-30 15:50:47', "toooooo");

-- VIDEO_PLATFORM.PLAYLIST TABLE 내보내기
DROP TABLE IF EXISTS `PLAYLIST`;
CREATE TABLE IF NOT EXISTS `PLAYLIST` (
`makerID` varchar(10) NOT NULL,
`listID` int NOT NULL AUTO_INCREMENT,
`listTitle` varchar(100) NOT NULL,
`listReportNum` int NOT NULL DEFAULT 0,
PRIMARY KEY (`listID`),
UNIQUE (`makerID`, `listTitle`),
FOREIGN KEY (`makerID`) REFERENCES `USER` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- VIDEO_PLATFORM.PLAYLIST TABLE의 데이터 내보내기
DELETE FROM `PLAYLIST`;
INSERT INTO `PLAYLIST` (`makerID`, `listTitle`, `listReportNum`) VALUES
	("user1", "playlist", 100),
    ("hyu", "my list", 100);

-- VIDEO_PLATFORM.COMSIST_OF TABLE 내보내기
CREATE TABLE `CONSIST_OF` (
`cListID` int NOT NULL,
`cVideoID` int NOT NULL,
PRIMARY KEY (`cListID`, `cVideoID`),
FOREIGN KEY (`cListID`) REFERENCES `PLAYLIST` (`listID`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`cVideoID`) REFERENCES `VIDEO` (`videoId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- VIDEO_PLATFORM.CONSIST_OF TABLE의 데이터 내보내기
DELETE FROM `CONSIST_OF`;
INSERT INTO `CONSIST_OF` (`cListID`, `cVideoID`) VALUES
	(1, 1),
    (1, 2);
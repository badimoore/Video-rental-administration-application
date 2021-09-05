-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 05, 2021 at 11:06 PM
-- Server version: 10.2.38-MariaDB
-- PHP Version: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `VIDEORENTAL`
--

-- --------------------------------------------------------

--
-- Table structure for table `Audiolanguage`
--

CREATE TABLE `Audiolanguage` (
  `AudiolanguageID` int(11) NOT NULL,
  `LanguageID` int(11) NOT NULL,
  `FilmID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Country`
--

CREATE TABLE `Country` (
  `CountryID` int(11) NOT NULL,
  `Name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Crew`
--

CREATE TABLE `Crew` (
  `CrewID` int(11) NOT NULL,
  `Firstname` varchar(40) NOT NULL,
  `Lastname` varchar(40) DEFAULT NULL,
  `DOB` date DEFAULT NULL,
  `Bio` text DEFAULT NULL,
  `Photo` mediumblob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Crewrole`
--

CREATE TABLE `Crewrole` (
  `CrewroleID` int(11) NOT NULL,
  `CrewID` int(11) NOT NULL,
  `RoleID` int(11) NOT NULL,
  `FilmID` int(11) NOT NULL,
  `Charactername` varchar(40) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Customer`
--

CREATE TABLE `Customer` (
  `CustomerID` int(11) NOT NULL,
  `Firstname` varchar(40) NOT NULL,
  `Lastname` varchar(40) NOT NULL,
  `Address` varchar(60) NOT NULL,
  `Postcode` varchar(10) NOT NULL,
  `City` varchar(30) NOT NULL,
  `Phone` varchar(20) NOT NULL,
  `Email` varchar(60) NOT NULL,
  `Joineddate` date NOT NULL DEFAULT current_timestamp(),
  `DOB` date NOT NULL,
  `Pwd` varchar(130) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `Salt` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `Displayname` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Film`
--

CREATE TABLE `Film` (
  `FilmID` int(11) NOT NULL,
  `TypeID` int(11) DEFAULT NULL,
  `StudioID` int(11) DEFAULT NULL,
  `RatingID` int(11) DEFAULT NULL,
  `Title` varchar(40) NOT NULL,
  `Year` smallint(6) NOT NULL,
  `Runtime` smallint(6) NOT NULL,
  `Description` text DEFAULT NULL,
  `Coverart` mediumblob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Filmcountry`
--

CREATE TABLE `Filmcountry` (
  `FilmcountryID` int(11) NOT NULL,
  `FilmID` int(11) NOT NULL,
  `CountryID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Filmgenre`
--

CREATE TABLE `Filmgenre` (
  `FilmgenreID` int(11) NOT NULL,
  `GenreID` int(11) NOT NULL,
  `FilmID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Filmmedia`
--

CREATE TABLE `Filmmedia` (
  `FilmmediaID` int(11) NOT NULL,
  `FilmID` int(11) NOT NULL,
  `MediaID` int(11) NOT NULL,
  `Stock` int(11) NOT NULL,
  `Price` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Filmratingkeyword`
--

CREATE TABLE `Filmratingkeyword` (
  `FilmratingkeywordID` int(11) NOT NULL,
  `FilmID` int(11) NOT NULL,
  `RatingkeywordID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Filmrental`
--

CREATE TABLE `Filmrental` (
  `FilmrentalID` int(11) NOT NULL,
  `CustomerID` int(11) NOT NULL,
  `PaymentmethodID` int(11) NOT NULL,
  `Rentdate` date NOT NULL DEFAULT current_timestamp(),
  `Rentlength` tinyint(4) NOT NULL,
  `Totalprice` float NOT NULL,
  `Returned` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Genre`
--

CREATE TABLE `Genre` (
  `GenreID` int(11) NOT NULL,
  `Name` varchar(40) NOT NULL,
  `Description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Language`
--

CREATE TABLE `Language` (
  `LanguageID` int(11) NOT NULL,
  `Name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Media`
--

CREATE TABLE `Media` (
  `MediaID` int(11) NOT NULL,
  `Name` varchar(30) NOT NULL,
  `Description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Paymentmethod`
--

CREATE TABLE `Paymentmethod` (
  `PaymentmethodID` int(11) NOT NULL,
  `Name` varchar(40) NOT NULL,
  `Description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Rating`
--

CREATE TABLE `Rating` (
  `RatingID` int(11) NOT NULL,
  `Designation` varchar(10) NOT NULL,
  `Minage` tinyint(4) NOT NULL,
  `Description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Ratingkeyword`
--

CREATE TABLE `Ratingkeyword` (
  `RatingkeywordID` int(11) NOT NULL,
  `Keyword` varchar(30) NOT NULL,
  `Icon` blob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Rentalitem`
--

CREATE TABLE `Rentalitem` (
  `RentalitemID` int(11) NOT NULL,
  `FilmrentalID` int(11) NOT NULL,
  `FilmmediaID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Review`
--

CREATE TABLE `Review` (
  `ReviewID` int(11) NOT NULL,
  `FilmID` int(30) NOT NULL,
  `CustomerID` int(11) NOT NULL,
  `Time` datetime NOT NULL,
  `Stars` float NOT NULL,
  `Content` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Role`
--

CREATE TABLE `Role` (
  `RoleID` int(11) NOT NULL,
  `Name` varchar(40) NOT NULL,
  `Description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Staff`
--

CREATE TABLE `Staff` (
  `Username` varchar(20) NOT NULL,
  `Firstname` varchar(30) NOT NULL,
  `Lastname` varchar(40) NOT NULL,
  `Email` varchar(40) NOT NULL,
  `Pwd` varchar(130) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `Salt` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Studio`
--

CREATE TABLE `Studio` (
  `StudioID` int(11) NOT NULL,
  `Name` varchar(40) NOT NULL,
  `Description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Subtitlelanguage`
--

CREATE TABLE `Subtitlelanguage` (
  `SubtitlelanguageID` int(11) NOT NULL,
  `LanguageID` int(11) NOT NULL,
  `FilmID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Type`
--

CREATE TABLE `Type` (
  `TypeID` int(11) NOT NULL,
  `Name` varchar(40) NOT NULL,
  `Description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Audiolanguage`
--
ALTER TABLE `Audiolanguage`
  ADD PRIMARY KEY (`AudiolanguageID`),
  ADD KEY `language` (`LanguageID`),
  ADD KEY `film` (`FilmID`);

--
-- Indexes for table `Country`
--
ALTER TABLE `Country`
  ADD PRIMARY KEY (`CountryID`);

--
-- Indexes for table `Crew`
--
ALTER TABLE `Crew`
  ADD PRIMARY KEY (`CrewID`);

--
-- Indexes for table `Crewrole`
--
ALTER TABLE `Crewrole`
  ADD PRIMARY KEY (`CrewroleID`),
  ADD KEY `participant` (`CrewID`),
  ADD KEY `crewrole` (`RoleID`),
  ADD KEY `film` (`FilmID`);

--
-- Indexes for table `Customer`
--
ALTER TABLE `Customer`
  ADD PRIMARY KEY (`CustomerID`);

--
-- Indexes for table `Film`
--
ALTER TABLE `Film`
  ADD PRIMARY KEY (`FilmID`),
  ADD KEY `type` (`TypeID`),
  ADD KEY `studio` (`StudioID`),
  ADD KEY `rating` (`RatingID`);

--
-- Indexes for table `Filmcountry`
--
ALTER TABLE `Filmcountry`
  ADD PRIMARY KEY (`FilmcountryID`),
  ADD KEY `film` (`FilmID`),
  ADD KEY `country` (`CountryID`);

--
-- Indexes for table `Filmgenre`
--
ALTER TABLE `Filmgenre`
  ADD PRIMARY KEY (`FilmgenreID`),
  ADD KEY `genre` (`GenreID`),
  ADD KEY `film` (`FilmID`);

--
-- Indexes for table `Filmmedia`
--
ALTER TABLE `Filmmedia`
  ADD PRIMARY KEY (`FilmmediaID`),
  ADD KEY `FilmID` (`FilmID`),
  ADD KEY `MediaID` (`MediaID`);

--
-- Indexes for table `Filmratingkeyword`
--
ALTER TABLE `Filmratingkeyword`
  ADD PRIMARY KEY (`FilmratingkeywordID`),
  ADD KEY `film` (`FilmID`),
  ADD KEY `keyword` (`RatingkeywordID`);

--
-- Indexes for table `Filmrental`
--
ALTER TABLE `Filmrental`
  ADD PRIMARY KEY (`FilmrentalID`),
  ADD KEY `Customer` (`CustomerID`),
  ADD KEY `Payment` (`PaymentmethodID`);

--
-- Indexes for table `Genre`
--
ALTER TABLE `Genre`
  ADD PRIMARY KEY (`GenreID`);

--
-- Indexes for table `Language`
--
ALTER TABLE `Language`
  ADD PRIMARY KEY (`LanguageID`);

--
-- Indexes for table `Media`
--
ALTER TABLE `Media`
  ADD PRIMARY KEY (`MediaID`);

--
-- Indexes for table `Paymentmethod`
--
ALTER TABLE `Paymentmethod`
  ADD PRIMARY KEY (`PaymentmethodID`);

--
-- Indexes for table `Rating`
--
ALTER TABLE `Rating`
  ADD PRIMARY KEY (`RatingID`);

--
-- Indexes for table `Ratingkeyword`
--
ALTER TABLE `Ratingkeyword`
  ADD PRIMARY KEY (`RatingkeywordID`);

--
-- Indexes for table `Rentalitem`
--
ALTER TABLE `Rentalitem`
  ADD PRIMARY KEY (`RentalitemID`),
  ADD KEY `filmrental` (`FilmrentalID`),
  ADD KEY `film` (`FilmmediaID`);

--
-- Indexes for table `Review`
--
ALTER TABLE `Review`
  ADD PRIMARY KEY (`ReviewID`),
  ADD KEY `film` (`FilmID`),
  ADD KEY `customer` (`CustomerID`);

--
-- Indexes for table `Role`
--
ALTER TABLE `Role`
  ADD PRIMARY KEY (`RoleID`);

--
-- Indexes for table `Staff`
--
ALTER TABLE `Staff`
  ADD PRIMARY KEY (`Username`),
  ADD UNIQUE KEY `email` (`Email`);

--
-- Indexes for table `Studio`
--
ALTER TABLE `Studio`
  ADD PRIMARY KEY (`StudioID`);

--
-- Indexes for table `Subtitlelanguage`
--
ALTER TABLE `Subtitlelanguage`
  ADD PRIMARY KEY (`SubtitlelanguageID`),
  ADD KEY `language` (`LanguageID`),
  ADD KEY `film` (`FilmID`);

--
-- Indexes for table `Type`
--
ALTER TABLE `Type`
  ADD PRIMARY KEY (`TypeID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Audiolanguage`
--
ALTER TABLE `Audiolanguage`
  MODIFY `AudiolanguageID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Country`
--
ALTER TABLE `Country`
  MODIFY `CountryID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Crew`
--
ALTER TABLE `Crew`
  MODIFY `CrewID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Crewrole`
--
ALTER TABLE `Crewrole`
  MODIFY `CrewroleID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Customer`
--
ALTER TABLE `Customer`
  MODIFY `CustomerID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Film`
--
ALTER TABLE `Film`
  MODIFY `FilmID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Filmcountry`
--
ALTER TABLE `Filmcountry`
  MODIFY `FilmcountryID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Filmgenre`
--
ALTER TABLE `Filmgenre`
  MODIFY `FilmgenreID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Filmmedia`
--
ALTER TABLE `Filmmedia`
  MODIFY `FilmmediaID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Filmratingkeyword`
--
ALTER TABLE `Filmratingkeyword`
  MODIFY `FilmratingkeywordID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Filmrental`
--
ALTER TABLE `Filmrental`
  MODIFY `FilmrentalID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Genre`
--
ALTER TABLE `Genre`
  MODIFY `GenreID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Language`
--
ALTER TABLE `Language`
  MODIFY `LanguageID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Media`
--
ALTER TABLE `Media`
  MODIFY `MediaID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Paymentmethod`
--
ALTER TABLE `Paymentmethod`
  MODIFY `PaymentmethodID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Rating`
--
ALTER TABLE `Rating`
  MODIFY `RatingID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Ratingkeyword`
--
ALTER TABLE `Ratingkeyword`
  MODIFY `RatingkeywordID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Rentalitem`
--
ALTER TABLE `Rentalitem`
  MODIFY `RentalitemID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Review`
--
ALTER TABLE `Review`
  MODIFY `ReviewID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Role`
--
ALTER TABLE `Role`
  MODIFY `RoleID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Studio`
--
ALTER TABLE `Studio`
  MODIFY `StudioID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Subtitlelanguage`
--
ALTER TABLE `Subtitlelanguage`
  MODIFY `SubtitlelanguageID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Type`
--
ALTER TABLE `Type`
  MODIFY `TypeID` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Audiolanguage`
--
ALTER TABLE `Audiolanguage`
  ADD CONSTRAINT `Audiolanguage_ibfk_1` FOREIGN KEY (`FilmID`) REFERENCES `Film` (`FilmID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Audiolanguage_ibfk_2` FOREIGN KEY (`LanguageID`) REFERENCES `Language` (`LanguageID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Crewrole`
--
ALTER TABLE `Crewrole`
  ADD CONSTRAINT `Crewrole_ibfk_1` FOREIGN KEY (`RoleID`) REFERENCES `Role` (`RoleID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Crewrole_ibfk_2` FOREIGN KEY (`FilmID`) REFERENCES `Film` (`FilmID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Crewrole_ibfk_3` FOREIGN KEY (`CrewID`) REFERENCES `Crew` (`CrewID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Film`
--
ALTER TABLE `Film`
  ADD CONSTRAINT `Film_ibfk_1` FOREIGN KEY (`StudioID`) REFERENCES `Studio` (`StudioID`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `Film_ibfk_2` FOREIGN KEY (`RatingID`) REFERENCES `Rating` (`RatingID`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `Film_ibfk_3` FOREIGN KEY (`TypeID`) REFERENCES `Type` (`TypeID`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `Filmcountry`
--
ALTER TABLE `Filmcountry`
  ADD CONSTRAINT `Filmcountry_ibfk_1` FOREIGN KEY (`FilmID`) REFERENCES `Film` (`FilmID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Filmcountry_ibfk_2` FOREIGN KEY (`CountryID`) REFERENCES `Country` (`CountryID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Filmgenre`
--
ALTER TABLE `Filmgenre`
  ADD CONSTRAINT `Filmgenre_ibfk_1` FOREIGN KEY (`FilmID`) REFERENCES `Film` (`FilmID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Filmgenre_ibfk_2` FOREIGN KEY (`GenreID`) REFERENCES `Genre` (`GenreID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Filmmedia`
--
ALTER TABLE `Filmmedia`
  ADD CONSTRAINT `Filmmedia_ibfk_2` FOREIGN KEY (`FilmID`) REFERENCES `Film` (`FilmID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Filmmedia_ibfk_3` FOREIGN KEY (`MediaID`) REFERENCES `Media` (`MediaID`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `Filmratingkeyword`
--
ALTER TABLE `Filmratingkeyword`
  ADD CONSTRAINT `Filmratingkeyword_ibfk_1` FOREIGN KEY (`FilmID`) REFERENCES `Film` (`FilmID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Filmratingkeyword_ibfk_2` FOREIGN KEY (`RatingkeywordID`) REFERENCES `Ratingkeyword` (`RatingkeywordID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Filmrental`
--
ALTER TABLE `Filmrental`
  ADD CONSTRAINT `Filmrental_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `Customer` (`CustomerID`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `Filmrental_ibfk_4` FOREIGN KEY (`PaymentmethodID`) REFERENCES `Paymentmethod` (`PaymentmethodID`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `Rentalitem`
--
ALTER TABLE `Rentalitem`
  ADD CONSTRAINT `Rentalitem_ibfk_1` FOREIGN KEY (`FilmrentalID`) REFERENCES `Filmrental` (`FilmrentalID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Rentalitem_ibfk_2` FOREIGN KEY (`FilmmediaID`) REFERENCES `Filmmedia` (`FilmmediaID`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `Review`
--
ALTER TABLE `Review`
  ADD CONSTRAINT `Review_ibfk_1` FOREIGN KEY (`FilmID`) REFERENCES `Film` (`FilmID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Review_ibfk_2` FOREIGN KEY (`CustomerID`) REFERENCES `Customer` (`CustomerID`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `Subtitlelanguage`
--
ALTER TABLE `Subtitlelanguage`
  ADD CONSTRAINT `Subtitlelanguage_ibfk_1` FOREIGN KEY (`FilmID`) REFERENCES `Film` (`FilmID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Subtitlelanguage_ibfk_2` FOREIGN KEY (`LanguageID`) REFERENCES `Language` (`LanguageID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 24. Mrz 2021 um 13:25
-- Server-Version: 10.4.11-MariaDB
-- PHP-Version: 7.4.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `tutorbot`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `account`
--

CREATE TABLE `account` (
  `username` varchar(64) NOT NULL,
  `password` varchar(256) NOT NULL,
  `id` int(11) NOT NULL,
  `role` int(11) NOT NULL,
  `realname` text NOT NULL,
  `email` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `account`
--

INSERT INTO `account` (`username`, `password`, `id`, `role`, `realname`, `email`) VALUES
('123', '3c9909afec25354d551dae21590bb26e38d53f2173b8d3dc3eee4c047e7ab1c1eb8b85103e3be7ba613b31bb5c9c36214dc9f14a42fd7a2fdb84856bca5c44c2', 1, 2, 'Jonas Reitz', 'jonas.reitz@mni.thm.de'),
('TutorBot', 'unreachable', 2, 2, 'Tutor Bot', 'tutorbotthm@gmail.com');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `answer`
--

CREATE TABLE `answer` (
  `id` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `exercise_id` int(11) NOT NULL,
  `answer` text NOT NULL,
  `lastChanged` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `answer`
--

INSERT INTO `answer` (`id`, `account_id`, `exercise_id`, `answer`, `lastChanged`) VALUES
(12, 1, 1, 'PUBLIC STATIC VOID MAIN(STRING[] ARGS){\r\n//HIER DIE KONSOLEN AUSGABE EINFÜGENdasda\r\n\r\n}', '22/02/2021 12:20');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `assigned_exercises`
--

CREATE TABLE `assigned_exercises` (
  `account_id` int(11) NOT NULL,
  `exercise_id` int(11) NOT NULL,
  `completed` varchar(5) NOT NULL,
  `due_date` text NOT NULL,
  `assignment_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `assigned_exercises`
--

INSERT INTO `assigned_exercises` (`account_id`, `exercise_id`, `completed`, `due_date`, `assignment_id`) VALUES
(1, 3, 'false', '25/02/2021 18:17', 15);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `exercise`
--

CREATE TABLE `exercise` (
  `id` int(11) NOT NULL,
  `head` text NOT NULL,
  `answers` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `exercise`
--

INSERT INTO `exercise` (`id`, `head`, `answers`) VALUES
(1, 'SCHREIBEN SIE EIN HELLO WORLD  2 PROGRAMM!', 'PUBLIC STATIC VOID MAIN(STRING[] ARGS){\r\n//HIER DIE KONSOLEN AUSGABE EINFÜGEN\r\n\r\n}'),
(2, 'uidfsab<öjsdfkbjasdfk', 'asdfsdfiuashdifuhasidufhi'),
(3, 'Schreiben Sie ein Hello World Programm!', 'public static void main(String[] args){\r\n//Hier die Konsolen Ausgabe einfügen\r\n\r\n}'),
(4, 'asdasdsdfgfds', 'Hallo');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `question`
--

CREATE TABLE `question` (
  `id` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `body` text NOT NULL,
  `title` text NOT NULL,
  `date` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `question`
--

INSERT INTO `question` (`id`, `account_id`, `body`, `title`, `date`) VALUES
(1, 1, 'Die erste Frage?', 'Der Titel der ersten Frage', 'Heute oder so'),
(2, 1, 'Titel Frage 2', 'Die 2 Frage auf dieser coolen Website', '24/02/2021 16:29'),
(3, 1, '123', '345', '24/02/2021 16:30');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `question_answers`
--

CREATE TABLE `question_answers` (
  `id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `body` text NOT NULL,
  `date` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `question_answers`
--

INSERT INTO `question_answers` (`id`, `question_id`, `account_id`, `body`, `date`) VALUES
(1, 1, 1, 'Die erste Antwort', '24/02/2021 16:18'),
(2, 1, 1, 'Hier um die Emails zu testen.', '24/02/2021 16:53'),
(3, 1, 1, 'Nochmal der email test', '24/02/2021 16:55');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `question_vote`
--

CREATE TABLE `question_vote` (
  `id` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `answer_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `question_vote`
--

INSERT INTO `question_vote` (`id`, `score`, `account_id`, `answer_id`) VALUES
(1, 1, 1, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `vote`
--

CREATE TABLE `vote` (
  `id` int(11) NOT NULL,
  `account_id` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `answer_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `vote`
--

INSERT INTO `vote` (`id`, `account_id`, `score`, `answer_id`) VALUES
(8, 1, 1, 12);

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `answer`
--
ALTER TABLE `answer`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `assigned_exercises`
--
ALTER TABLE `assigned_exercises`
  ADD PRIMARY KEY (`assignment_id`);

--
-- Indizes für die Tabelle `exercise`
--
ALTER TABLE `exercise`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `question`
--
ALTER TABLE `question`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `question_answers`
--
ALTER TABLE `question_answers`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `question_vote`
--
ALTER TABLE `question_vote`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `vote`
--
ALTER TABLE `vote`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `account`
--
ALTER TABLE `account`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT für Tabelle `answer`
--
ALTER TABLE `answer`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT für Tabelle `assigned_exercises`
--
ALTER TABLE `assigned_exercises`
  MODIFY `assignment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT für Tabelle `exercise`
--
ALTER TABLE `exercise`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT für Tabelle `question`
--
ALTER TABLE `question`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT für Tabelle `question_answers`
--
ALTER TABLE `question_answers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT für Tabelle `question_vote`
--
ALTER TABLE `question_vote`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT für Tabelle `vote`
--
ALTER TABLE `vote`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

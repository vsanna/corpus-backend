CREATE TABLE IF NOT EXISTS `filtered_words`
(
    `id`         INT(20)      NOT NULL AUTO_INCREMENT,
    `word`       VARCHAR(500) NOT NULL NOT NULL UNIQUE,
    `reason`     int          NOT NULL DEFAULT 0,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`created_at`),
    INDEX (`updated_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


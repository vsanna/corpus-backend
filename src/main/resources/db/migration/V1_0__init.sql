CREATE TABLE IF NOT EXISTS `crawler_articles`
(
    `id`             INT(20)       NOT NULL AUTO_INCREMENT,
    `url`            VARCHAR(500)  NOT NULL,
    `title`          VARCHAR(1000) NOT NULL,
    `body`           TEXT,
    `fetched_at`     DATETIME      NOT NULL,
    `html_file_name` VARCHAR(500),
    `parse_status`   INT           NOT NULL DEFAULT 0,
    `media_type`     INT           NOT NULL DEFAULT 0,
    `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`fetched_at`),
    INDEX (`created_at`),
    INDEX (`updated_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
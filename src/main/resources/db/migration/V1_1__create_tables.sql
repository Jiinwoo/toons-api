CREATE TABLE IF NOT EXISTS `tb_webtoon`
(
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title`         VARCHAR(255) NOT NULL,
    `thumbnail_url` VARCHAR(255) NOT NULL,
    `day_of_week`   VARCHAR(255) NOT NULL,
    `platform`      VARCHAR(255) NOT NULL,
    `platform_id`   VARCHAR(255) NOT NULL,
    `link`          VARCHAR(255) NOT NULL,
    `completed`     BOOLEAN      NOT NULL DEFAULT FALSE,
    `created_at`    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_platform_with_id (`platform_id`, `platform`)
);

CREATE TABLE `tb_member`
(
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`           VARCHAR(255) NOT NULL,
    `verified_email` VARCHAR(255) NULL,
    `subscribe`      BOOLEAN      NOT NULL DEFAULT FALSE,
    `provider`       VARCHAR(50)  NOT NULL,
    `provider_id`    VARCHAR(255) NOT NULL,
    `created_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_provider_with_id (`provider_id`, `provider`)
);

CREATE TABLE `tb_alarm`
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `member_id`  BIGINT      NOT NULL,
    `webtoon_id` BIGINT      NOT NULL,
    `status`     VARCHAR(50) NOT NULL,
    `send_at`    TIMESTAMP,
    `created_at` TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY fk_alarm_member_id (`member_id`) REFERENCES `tb_member` (`id`),
    FOREIGN KEY fk_alarm_webtoon_id (`webtoon_id`) REFERENCES `tb_webtoon` (`id`)
);

CREATE TABLE `tb_board`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY,
    `content_id`   BIGINT      NULL,
    `content_type` VARCHAR(50) NOT NULL,
    `created_at`   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `tb_post`
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `board_id`   BIGINT       NOT NULL,
    `member_id`  BIGINT       NOT NULL,
    `tag`        VARCHAR(255) NULL,
    `title`      VARCHAR(255) NOT NULL,
    `content`    TEXT,
    `like_count` INT          NOT NULL DEFAULT 0,
    `deleted_at` TIMESTAMP,
    `created_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY fk_board_member_id (`member_id`) REFERENCES `tb_member` (`id`),
    FOREIGN KEY fk_post_board_id (`board_id`) REFERENCES `tb_board` (`id`)
);

CREATE TABLE `tb_post_like`
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `post_id`    BIGINT   NOT NULL,
    `member_id`  BIGINT   NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_post_user (`post_id`, `member_id`),
    FOREIGN KEY (`post_id`) REFERENCES tb_post (`id`) ON DELETE CASCADE
);

CREATE INDEX idx_post_likes_post_id ON `tb_post_like` (`post_id`);
CREATE INDEX idx_post_likes_member_id ON `tb_post_like` (`member_id`);


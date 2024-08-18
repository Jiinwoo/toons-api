CREATE TABLE IF NOT EXISTS `tb_webtoon`
(
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title`         VARCHAR(255) NOT NULL,
    `thumbnail_url` VARCHAR(255) NOT NULL,
    `day_of_week`   VARCHAR(255) NOT NULL,
    `platform`      VARCHAR(255) NOT NULL,
    `platform_id`   VARCHAR(255) NOT NULL,
    `link`          VARCHAR(255) NOT NULL,
    `created_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_platform_with_id (`platform_id`, `platform`)
);

CREATE TABLE `tb_member`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(255) NOT NULL,
    `provider`    VARCHAR(50)  NOT NULL,
    `provider_id` VARCHAR(255) NOT NULL,
    `created_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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


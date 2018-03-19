USE plp;

DROP TABLE IF EXISTS plp.ia_organisations;
CREATE TABLE plp.ia_organisations (
  `organisation_id` INT(11) NOT NULL AUTO_INCREMENT,
  `organisation_name` VARCHAR(64) NOT NULL,
  `key` VARCHAR(600) NOT NULL DEFAULT '',
  `insights_key` VARCHAR(600) NOT NULL DEFAULT '',
  `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created` TIMESTAMP NOT NULL,
  `license_end_date` DATE NOT NULL,
  PRIMARY KEY  (`organisation_id`),
  UNIQUE KEY `ia_organisation_settings_1` (`organisation_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS plp.ia_ipaddress_for_organisation;
CREATE TABLE plp.ia_ipaddress_for_organisation (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `organisation_id` INT(11) NOT NULL,
  `ip_address_range_start` BIGINT(11) NOT NULL,
  `ip_address_range_end` BIGINT(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ia_organisation_ip_addresses_1` (`organisation_id`, `ip_address_range_start`, `ip_address_range_end`),
  CONSTRAINT `ia_ipaddress_for_organisation_kyc_organisations` FOREIGN KEY (`organisation_id`) REFERENCES `ia_organisations` (`organisation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS plp.ia_user;
CREATE TABLE plp.ia_user (
  `user_id` INT(11) NOT NULL AUTO_INCREMENT,
  `organisation_id` INT(11) NOT NULL,
  `email_id` VARCHAR(60) NOT NULL UNIQUE,
  `password` CHAR(50) DEFAULT NULL,
  `status` ENUM('NORMAL', 'LOCKED') NOT NULL DEFAULT 'NORMAL',
  `updated` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created` TIMESTAMP NOT NULL,
  `last_login` TIMESTAMP NOT NULL,
   PRIMARY KEY (`user_id`),
   CONSTRAINT `ia_ipaddress_for_organisation_ia_organisations` FOREIGN KEY (`organisation_id`) REFERENCES `ia_organisations` (`organisation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS plp.ia_transactions;
CREATE TABLE plp.ia_transactions (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `transaction_id` VARCHAR(64) NOT NULL,
  `perfios_transcation_id` VARCHAR(64) DEFAULT NULL,
  `user_id` INT(11) NOT NULL,
  `generated_link` VARCHAR(254) DEFAULT NULL,
  `form_data_json` VARCHAR(1024) DEFAULT NULL,
  `status` ENUM(
    'INITIATED',
    'INIT_RESPONSE_RECEIVED',
    'EMAIL_SENT',
    'PROCESSING',
    'REPORT_DELIVERY_FAILED',
    'COMPLETED',
    'ERROR',
    'EXPIRED') NOT NULL DEFAULT 'INITIATED',
  `insights_status` VARCHAR(256) DEFAULT NULL,
  `message` VARCHAR(1024) DEFAULT NULL,
  `report_location` VARCHAR(1024) DEFAULT NULL, 
  `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
  `updated` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created` TIMESTAMP NOT NULL,
  `expiry_date` TIMESTAMP,
  PRIMARY KEY  (`id`),
  KEY `ia_transactions_1` (`transaction_id`),
  KEY `ia_transactions_2` (`perfios_transcation_id`),
  CONSTRAINT `ia_transactions_ia_user` FOREIGN KEY (`user_id`) REFERENCES `ia_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

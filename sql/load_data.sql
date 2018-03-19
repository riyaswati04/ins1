INSERT INTO plp.ia_organisations
VALUES (
    1,
    'acme',
    '-----BEGIN RSA PRIVATE KEY-----\
MIIBPQIBAAJBAOA7ZMwwq9OY8todlYBwsziy1v7MLUmVza7Qdp+44G7eI0huBTLR\
do76VLoiY+zLg0jImwUsFVq474bK7HvDoOUCAwEAAQJBAJTsHWfXs2bXIANovpAN\
SZqQfGXBKRrEGVTPMtmlqbk3JJcpNMK9gxqWbeo+ZmPBgHS/CmSJxvOTnNOiLsBe\
lwECIQD+g2Mum+j77ejJZHZTAzTXVeq7LT8NYQBJbBlW3cptoQIhAOGKuN+ftI7E\
WUjMdzX3GuAv5YOBd1sz/gZvxSfsTsTFAiEAg0Yrgx/htQfKOQ47RafyulrTXsYA\
rprotfYuv7JYNeECIQCp6TP1Y/9GPq10pnR4dzwMAIlLVNFyJ+0LNFC3DtMYcQIh\
AIeLvybuqaaaNBzrXQrjgHGANrn2oHyhdM5FUK5nq3n5\
-----END RSA PRIVATE KEY-----',
    '',
    TRUE,
    NOW(),
    NOW(),
    NOW()
    );

INSERT INTO ia_user VALUES(1,1,'ramasr@perfios.com',sha1('falcon123'),'NORMAL',now(),now(),now());
INSERT INTO ia_user VALUES(2,1,'staff@perfios.com',sha1('falcon123'),'normal',now(),now(),now());

UPDATE ia_organisations SET insights_key = (SELECT private_key FROM plp.i_organisations WHERE name="acme" );

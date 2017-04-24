ALTER TABLE PAYMENT ADD USER_GROUP_ID INT NULL;
ALTER TABLE PAYMENT ADD CONSTRAINT payment_user_group_idx FOREIGN KEY (USER_GROUP_ID) REFERENCES USER_GROUP (ID) ON DELETE CASCADE;
UPDATE PAYMENT p SET USER_GROUP_ID=(SELECT ID FROM USER_GROUP WHERE PAYMENT_ID=p.id);

-- SELECT * FROM USER_GROUP WHERE GROUP_ID=1;
-- UPDATE PAYMENT p SET USER_GROUP_ID=210 WHERE USER_GROUP_ID IS NULL;

ALTER TABLE PAYMENT ALTER COLUMN USER_GROUP_ID INTEGER NOT NULL;
ALTER TABLE USER_GROUP DROP COLUMN PAYMENT_ID;


-- DML
-- (테스트 데이터 삽입)
INSERT INTO users (id, created_at, updated_at, username, email, password, profile_id)
VALUES(
          'a0000000-0000-0000-0000-000000000000',
          '2025-08-14 07:19:50.203854',
          '2025-08-14 07:19:50.203854',
          'testUser1',
          'testUser1@gmail.com',
          'testUser1234',
          null
      ),
      (
          'b0000000-0000-0000-0000-000000000000',
          '2025-08-14 07:25:50.203854',
          '2025-08-14 07:25:50.203854',
          'testUser2',
          'testUser2@gmail.com',
          'testUser1234',
          null
      );
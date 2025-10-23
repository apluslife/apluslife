# 라이프뉴스

## 소개

라이프뉴스는 공지사항과 보도자료를 포함하는 게시판입니다.

## 데이터 모델

### aplus\_라이프뉴스

| 컬럼    | 타입     | 설명        | 비고 |
| ------- | -------- | ----------- | ---- |
| idx     | int      | 고유 식별자 | PK   |
| title   | varchar  | 제목        |      |
| content | text     | 내용        |      |
| rDate   | datetime | 등록일      |      |
| uDate   | datetime | 수정일      |      |

### aplus\_파일업로드

| 컬럼         | 타입    | 설명            | 비고                                   |
| ------------ | ------- | --------------- | -------------------------------------- |
| idx          | int     | 고유 식별자     | PK                                     |
| fileName     | varchar | 파일명          |                                        |
| filePath     | varchar | 파일경로        |                                        |
| categoryIdx  | int     | 카테고리 식별자 | 1,2,3...                               |
| categoryName | varchar | 카테고리명      | 공시자료(1), 라이프뉴스(2), 기타(3)... |

## API 명세

### 라이프뉴스 목록 조회

```sql
SELECT idx, title, content, uDate
FROM LifeWeb..aplus_라이프뉴스
ORDER BY uDate DESC;
```

### 라이프뉴스 등록

```sql
-- 라이프뉴스 등록
INSERT INTO LifeWeb..aplus_라이프뉴스 (title, content, rDate, uDate)
VALUES (#{title}, #{content}, GETDATE(), GETDATE());

-- 파일 업로드가 있는 경우
INSERT INTO LifeWeb..aplus_파일업로드 (fileName, filePath, categoryIdx, categoryName)
VALUES (#{fileName}, #{filePath}, 2, '라이프뉴스');
```

### 라이프뉴스 수정

```sql
-- 라이프뉴스 수정
UPDATE a
SET title = #{title}, content = #{content}, uDate = GETDATE()
FROM LifeWeb..aplus_라이프뉴스 a
WHERE idx = #{idx};

-- 파일 업로드가 있는 경우
INSERT INTO LifeWeb..aplus_파일업로드 (fileName, filePath, categoryIdx, categoryName)
VALUES (#{fileName}, #{filePath}, 2, '라이프뉴스');
```

### 라이프뉴스 선택 삭제

```sql
DELETE
FROM LifeWeb..aplus_라이프뉴스
WHERE idx IN (#{selectedIdx1}, #{selectedIdx2}, #{selectedIdx3});
```

### 라이프뉴스 제목 검색

```sql
SELECT idx, title, content, uDate
FROM LifeWeb..aplus_라이프뉴스
WHERE title LIKE '%#{searchText}%'
```

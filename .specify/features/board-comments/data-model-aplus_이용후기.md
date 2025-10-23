# 🌟 Customer Reviews (Testimonials) Management

This document outlines the table structure, sample data, and SQL queries for managing customer reviews.

## 1. Table Schema

**`LifeWeb.aplus_이용후기` (Customer Reviews)**

| Column | Description |
| :--- | :--- |
| `idx` | Primary Key |
| `name` | Author's Name |
| `title` | Title |
| `content` | Content |
| `rDate` | Registration Date |
| `uDate` | Modification Date |
| `usedYN` | Approval Status (Y/N) |
| `hangsaCode` | Event Number |

---

## 2. Sample Data

| idx | name | title | content | rDate | uDate | usedYN | hangsaCode |
| :-- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | 모성래 | 효담상조 이용후기 (Hyodam Sangjo Review) | `<p>&nbsp;</p> <br /><img class="edit_image" style="width: 100%;" src="https://nhs.apluslife.co.kr/upload/etcInfo/reviewFile/0ec7e292-d415-4b9a-a63b-593accb87da0.jpg" alt="" border="0" /><br /><br />` | 33:15.0 | 33:15.0 | Y | 2025090700001 |
| 2 | 안덕장 | 효담상조 이용후기 (Hyodam Sangjo Review) | `<p>&nbsp;</p> <br /><img class="edit_image" style="width: 100%;" src="https://nhs.apluslife.co.kr/upload/etcInfo/reviewFile/9f740874-81b7-44f0-b1db-5332da37b36a.jpeg" alt="" border="0" /><br /><br />` | 02:00.6 | 02:00.6 | Y | 2025090700009 |
| 3 | 문원주 | 효담상조 이용후기 (Hyodam Sangjo Review) | `<p>&nbsp;</p> <br /><img class="edit_image" style="width: 100%;" src="https://nhs.apluslife.co.kr/upload/etcInfo/reviewFile/919d362a-a12c-44da-b4a8-44b06b3fc757.jpg" alt="" border="0" /><br /><br />` | 51:12.5 | 51:12.5 | N | 2025090900059 |

*Note: The `rDate` and `uDate` columns appear to contain timestamp fragments instead of full dates in the sample data.*

---

## 3. List View

```sql
SELECT idx, name, title, content, uDate, usedYN
FROM LifeWeb.aplus_이용후기
ORDER BY uDate DESC;
```

---

## 4. Approve Reviews (by selection)

```sql
UPDATE A
SET usedYN = 'Y', uDate = GETDATE()
FROM LifeWeb.aplus_이용후기 A
WHERE idx IN (#{selectedIdx1}, ..., #{selectedIdxN});
```

---

## 5. Modify Review

```sql
UPDATE A
SET title = #{title}, content = #{content}, uDate = GETDATE()
FROM LifeWeb.aplus_이용후기 A
WHERE idx = #{idx};
```

---

## 6. Delete Reviews (by selection)

```sql
DELETE
FROM LifeWeb.aplus_이용후기
WHERE idx IN (#{selectedIdx1}, ..., #{selectedIdxN});
```

---

## 7. Search Reviews (by Author's Name)

```sql
SELECT idx, name, title, content, uDate, usedYN
FROM LifeWeb.aplus_이용후기
WHERE name LIKE '%#{searchName}%'
ORDER BY uDate DESC;
```
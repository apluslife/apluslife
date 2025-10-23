# 📝 Other (Etc.) Management

This document outlines the table structure, data examples, and SQL queries for the "Other" (기타) section.

## 1. Table Schema

**`LifeWeb.aplus_기타` (Etc.)**

| Column | Description |
| :--- | :--- |
| `idx` | Primary Key |
| `title` | Title |
| `content` | Content |
| `rDate` | Registration Date |
| `uDate` | Modification Date |

**`LifeWeb.aplus_파일업로드` (File Uploads)**

| Column | Description | Notes |
| :--- | :--- | :--- |
| `idx` | Primary Key | |
| `fileName` | File Name | |
| `filePath` | File Path | |
| `categoryIdx`| Category ID | e.g., 1, 2, 3... |
| `categoryName`| Category Name | e.g., Official Announcement(1), Life News(2), Other(3) |

---

## 2. Sample Data

(No sample data provided)

---

## 3. List View

```sql
SELECT idx, title, content, uDate
FROM LifeWeb.aplus_기타
ORDER BY uDate DESC;
```

---

## 4. Create "Other" Post

### Step 1: Insert Post Content

```sql
INSERT INTO LifeWeb.aplus_기타 (title, content, rDate, uDate)
VALUES (#{title}, #{content}, GETDATE(), GETDATE());
```

### Step 2: Handle File Upload (If any)

If a file is uploaded, add its information to the file upload table.

```sql
INSERT INTO LifeWeb.aplus_파일업로드 (fileName, filePath, categoryIdx, categoryName)
VALUES (#{fileName}, #{filePath}, 3, '기타');
```

---

## 5. Modify "Other" Post

### Step 1: Update Post Content

```sql
UPDATE a
SET title = #{title}, content = #{content}, uDate = GETDATE()
FROM LifeWeb.aplus_기타 a
WHERE idx = #{idx};
```

### Step 2: Handle File Upload (If any)

If a file is uploaded during the update, add its information to the file upload table.

```sql
INSERT INTO LifeWeb.aplus_파일업로드 (fileName, filePath, categoryIdx, categoryName)
VALUES (#{fileName}, #{filePath}, 3, '기타');
```

---

## 6. Delete "Other" Posts (by selection)

```sql
DELETE
FROM LifeWeb.aplus_기타
WHERE idx IN (#{selectedIdx1}, ..., #{selectedIdxN});
```

---

## 7. Search "Other" Posts (by title)

```sql
SELECT idx, title, content, uDate
FROM LifeWeb.aplus_기타
WHERE title LIKE '%#{searchText}%'
```
*Note: There appears to be a typo in the original query (`titlte`). It has been corrected to `title` here.*
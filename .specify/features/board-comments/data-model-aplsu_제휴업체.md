# 🤝 Partner Company Management

This document outlines the table structure, sample data, and SQL queries for managing partner companies.

## 1. Table Schema

**`LifeWeb.aplus_제휴업체` (Partner Companies)**

| Column | Description | Notes |
| :--- | :--- | :--- |
| `idx` | Primary Key | |
| `gubun` | Category | 1: Public Institution, 2: General Enterprise, 3: Financial Company, 4: Association |
| `orgName` | Company Name | |
| `orgLogoImg` | Company Logo Image | e.g., `orgLogo_#{idx}.gif` |
| `orderIdx` | Display Order | 0: Hidden (partnership ended), 1~: Display priority |

**`LifeWeb.aplus_파일업로드` (File Uploads)**

| Column | Description | Notes |
| :--- | :--- | :--- |
| `idx` | Primary Key | |
| `fileName` | File Name | |
| `filePath` | File Path | |
| `categoryIdx`| Category ID | e.g., 1, 2, 3... |
| `categoryName`| Category Name | e.g., Official Announcement(1), Life News(2), Other(3), Conversion Service(4), Partner Company(5) |

---

## 2. Sample Data

**`LifeWeb.aplus_제휴업체`**

| idx | gubun | orgName | orgLogoImg | orderIdx |
| :-- | :---- | :--- | :--- | :--- |
| 1 | 1 | 국가보훈부 (Ministry of Patriots and Veterans Affairs) | `orgLogo_1.gif` | 1 |
| 2 | 1 | 한국전력공사 (Korea Electric Power Corporation) | `orgLogo_2.gif` | 2 |
| 3 | 2 | 기아 (Kia) | `orgLogo_3.gif` | 99 |
| 4 | 1 | 한국조폐공사 (Korea Minting and Security Printing Corporation) | `orgLogo_4.gif` | 1 |
| 5 | 3 | KEB하나은행 (KEB Hana Bank) | `orgLogo_5.gif` | 4 |

---

## 3. List View

Retrieve a list of partner companies. Can be filtered by the `gubun` category using radio buttons, with "All" as the default.

```sql
SELECT gubun, orgName, orgLogoImg, orderIdx
FROM LifeWeb.aplus_제휴업체
WHERE gubun = #{gubun}
```

---

## 4. New Partner Company Registration

### Step 1: Insert Partner Information

```sql
INSERT INTO LifeWeb.aplus_제휴업체 (gubun, orgName, orgLogoImg, orderIdx)
VALUES (#{gubun}, #{orgName}, #{orgLogoImg}, #{orderIdx});
```

### Step 2: Upload Logo and Save File Information

After the logo image is uploaded to the server, its information is saved to the database.

```sql
INSERT INTO LifeWeb.aplus_파일업로드 (fileName, filePath, categoryId, categoryName)
VALUES (#{orgLogoImg}, #{filePath}, '5', '제휴업체');
```
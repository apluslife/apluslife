# 🕊️ Obituary Notification Management

This document outlines the table structure, sample data, and SQL queries for managing obituary notifications.

## 1. Table Schema

**`LifeWeb.aplus_부고알림` (Obituary Notifications)**

| Column | Description | Notes |
| :--- | :--- | :--- |
| `idx` | Primary Key | |
| `name` | Deceased's Name | |
| `nameId` | Chief Mourner's Name | |
| `deceaseDate` | Date of Passing | |
| `funeralHall` | Funeral Hall Location | |

---

## 2. Sample Data

| idx | name | nameId | deceaseDate | funeralHall |
| :-- | :--- | :--- | :--- | :--- |
| 1 | 유수명 | 유종현 | 2025-09-14 | 강원도강릉의료원장례식장 (Gangwon-do Gangneung Medical Center Funeral Hall) |
| 2 | 황치연 | 황인재 | 2025-09-14 | 참사랑병원장례식장 (Chamsarang Hospital Funeral Hall) |
| 3 | 이상문 | 이해섭 | 2025-09-15 | 인하대병원장례식장 (Inha University Hospital Funeral Hall) |

---

## 3. Business Logic & Notes

*   **Admin Page:**
    *   The "Create New Notification" feature has been removed from the admin page.
    *   Available functions are: selective deletion and search (by the deceased's name only).
*   **Data Masking:**
    *   The chief mourner's name (`nameId`) must be masked in the middle on both the public website and the admin page.
    *   Example: "홍길동" should be displayed as "홍O동".

---

## 4. List View

```sql
SELECT name, nameId, deceaseDate, funeralHall
FROM LifeWeb.aplus_부고알림
ORDER BY idx DESC;
```

---

## 5. Delete Notifications (by selection)

```sql
DELETE
FROM LifeWeb.aplus_부고알림
WHERE idx IN (#{selectedIdx1}, #{selectedIdx2}, ...);
```

---

## 6. Search Notifications (by Deceased's Name)

```sql
SELECT name, nameId, deceaseDate, funeralHall
FROM LifeWeb.aplus_부고알림
WHERE name LIKE '%#{searchText}%'
```
*Note: There appears to be a typo in the original query (`LIKFE`). It has been corrected to `LIKE` here.*
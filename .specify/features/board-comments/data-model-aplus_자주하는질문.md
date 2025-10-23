# ❓ Frequently Asked Questions (FAQ) Management

This document outlines the table structure, sample data, and SQL queries for managing the FAQ section.

## 1. Table Schema

**`LifeWeb.aplus_자주하는질문` (FAQ)**

| Column | Description | Notes |
| :--- | :--- | :--- |
| `idx` | Primary Key | |
| `gubun` | Category Code | 1: Membership Inquiry, 2: General Contract, 3: Corporate Contract, 4: Payment, 5: Change/Cancellation, 6: Life Service, 7: Benefits, 8: Other |
| `gubunName`| Category Name | (Matches the `gubun` code) |
| `question` | Question | |
| `answer` | Answer | |

---

## 2. Sample Data

| idx | gubun | gubunName | question | answer |
| :-- | :-- | :--- | :--- | :--- |
| 1 | 3 | Corporate Contract | How do I inquire about corporate/group services? | Please use the online inquiry form or call our call center at 1688-8860, and a corporate manager will contact you immediately. |
| 2 | 6 | Life Service | What is the "Conversion Service"? | It is an A+ Life service that allows you to use necessary services by using part or 100% of your prepaid funeral installments. |
| 3 | 7 | Benefits | What benefits do members receive? | You can enjoy various life member benefits such as scare services, Golf Group Nabi Tour, real estate analysis service "Eolmani", and special hotel discounts. |

---

## 3. Business Logic & Notes

*   **List View:** The list is filtered by the category `gubun` using tabs.
*   **New FAQ:** The category (`gubunName`) should be a required field, selectable via a dropdown/selectbox.
*   **Search:** The search functionality should check for the search term in both the `question` and `answer` fields.

---

## 4. List View (by category)

```sql
SELECT gubunName, question, answer
FROM LifeWeb.aplus_자주하는질문
WHERE gubun = #{gubun}
```

---

## 5. Create FAQ

```sql
INSERT INTO LifeWeb.aplus_자주하는질문 (gubun, gubunName, question, answer)
VALUES (#{gubun}, #{gubunName}, #{question}, #{answer});
```

---

## 6. Modify FAQ

```sql
UPDATE A
SET gubun = #{gubun}, gubunName = #{gubunName}, question = #{question}, answer = #{answer}
FROM LifeWeb.aplus_자주하는질문 A
WHERE idx = #{idx}
```

---

## 7. Delete FAQ

```sql
DELETE
FROM LifeWeb.aplus_자주하는질문
WHERE idx = #{idx}
```

---

## 8. Search FAQ (in Question or Answer)

```sql
SELECT gubunName, question, answer
FROM LifeWeb.aplus_자주하는질문
WHERE gubun = #{gubun}
  AND (question LIKE '%#{searchText}%' OR answer LIKE '%#{searchText}%');
```
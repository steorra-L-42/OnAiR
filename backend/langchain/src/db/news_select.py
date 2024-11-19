import sqlite3
import os

base_dir = os.path.dirname(os.path.abspath(__file__))
db_path = os.path.join(base_dir, 'news.db')

conn = sqlite3.connect(db_path)
conn.row_factory = sqlite3.Row

c = conn.cursor()

count = c.execute("SELECT COUNT(*) FROM news").fetchone()[0]
print(f"Total news count : {count}")

rows = c.execute("SELECT * FROM news").fetchall()
for row in rows:
    print("--------------------")
    print(f"ID : {row['id']}")
    print(f"Title : {row['title']}")
    print(f"Summary : {row['summary']}")
    print(f"Created At : {row['created_at']}")
    print(f"Topic Id : {row['topic_id']}")

conn.close()
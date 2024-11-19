import sqlite3
import os

base_dir = os.path.dirname(os.path.abspath(__file__))
db_path = os.path.join(base_dir, 'weather.db')

conn = sqlite3.connect(db_path)
conn.row_factory = sqlite3.Row

c = conn.cursor()

count = c.execute("SELECT COUNT(*) FROM weather").fetchone()[0]
print(f"Total weather count : {count}")

rows = c.execute("SELECT * FROM weather").fetchall()
for row in rows:
    print("--------------------")
    print(f"ID : {row['id']}")
    print(f"Content : {row['content']}")
    print(f"Created At : {row['created_at']}")

conn.close()
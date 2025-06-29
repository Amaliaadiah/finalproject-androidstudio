from sqlalchemy import Column, Integer, String, Text
from app.database import Base

class Pet(Base):
    __tablename__ = "pets"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, index=True)
    type = Column(String)       # contoh: "kucing", "anjing"
    age = Column(Integer)
    description = Column(Text)
    image_url = Column(String, nullable=True)  # kalau mau pakai gambar

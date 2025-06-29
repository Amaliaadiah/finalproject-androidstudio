from sqlalchemy import Column, Integer, String, ForeignKey, Enum
from sqlalchemy.orm import relationship
from app.database import Base
import enum

class AdoptionStatus(str, enum.Enum):
    pending = "pending"
    approved = "approved"
    rejected = "rejected"

class Adoption(Base):
    __tablename__ = "adoptions"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    pet_id = Column(Integer, ForeignKey("pets.id"))
    status = Column(Enum(AdoptionStatus), default=AdoptionStatus.pending)

    # Optional relasi (biar gampang fetch user/pet info)
    user = relationship("User")
    pet = relationship("Pet")

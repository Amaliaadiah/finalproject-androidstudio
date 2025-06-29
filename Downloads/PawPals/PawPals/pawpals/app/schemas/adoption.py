from pydantic import BaseModel
from enum import Enum

class StatusEnum(str, Enum):
    pending = "pending"
    approved = "approved"
    rejected = "rejected"

class SimpleUser(BaseModel):
    id: int
    username: str

    class Config:
        orm_mode = True

class SimplePet(BaseModel):
    id: int
    name: str
    type: str | None = None
    age: int | None = None
    description: str | None = None
    image_url: str | None = None

    class Config:
        orm_mode = True


class AdoptionCreate(BaseModel):
    pet_id: int

class AdoptionUpdate(BaseModel):
    status: StatusEnum

class AdoptionOut(BaseModel):
    id: int
    status: StatusEnum
    user: SimpleUser
    pet: SimplePet

    class Config:
        orm_mode = True

from pydantic import BaseModel
from typing import Optional

class PetBase(BaseModel):
    name: str
    type: str
    age: int
    description: str
    image_url: Optional[str] = None

class PetCreate(PetBase):
    pass

class PetUpdate(PetBase):
    pass

class PetOut(PetBase):
    id: int
    class Config:
        orm_mode = True

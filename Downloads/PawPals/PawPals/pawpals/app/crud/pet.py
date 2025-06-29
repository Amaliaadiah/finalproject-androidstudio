from sqlalchemy.orm import Session
from app.models.pet import Pet
from app.schemas.pet import PetCreate, PetUpdate

def create_pet(db: Session, pet: PetCreate):
    new_pet = Pet(**pet)
    db.add(new_pet)
    db.commit()
    db.refresh(new_pet)
    return new_pet

def get_pets(db: Session):
    return db.query(Pet).all()

def get_pet_by_id(db: Session, pet_id: int):
    return db.query(Pet).filter(Pet.id == pet_id).first()

def update_pet(db: Session, pet_id: int, pet_data: PetUpdate):
    pet = get_pet_by_id(db, pet_id)
    if pet:
        for key, value in pet_data.dict().items():
            setattr(pet, key, value)
        db.commit()
        db.refresh(pet)
    return pet

def delete_pet(db: Session, pet_id: int):
    pet = get_pet_by_id(db, pet_id)
    if pet:
        db.delete(pet)
        db.commit()
    return pet

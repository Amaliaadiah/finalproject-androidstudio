from sqlalchemy.orm import Session, joinedload
from app.models.adoption import Adoption
from app.schemas.adoption import AdoptionCreate, AdoptionUpdate
from fastapi import HTTPException

def create_adoption(db: Session, user_id: int, pet_id: int):
    # ✅ Cek apakah user udah ajukan adopsi untuk pet ini
    existing = db.query(Adoption).filter(
        Adoption.user_id == user_id,
        Adoption.pet_id == pet_id
    ).first()
    
    if existing:
        raise HTTPException(status_code=400, detail="Kamu sudah mengajukan adopsi untuk hewan ini.")

    adoption = Adoption(user_id=user_id, pet_id=pet_id)
    db.add(adoption)
    db.commit()
    db.refresh(adoption)
    return adoption

def get_user_adoptions(db: Session, user_id: int):
    # ✅ JOIN agar field image_url & username ikut
    return (
        db.query(Adoption)
        .options(joinedload(Adoption.pet), joinedload(Adoption.user))
        .filter(Adoption.user_id == user_id)
        .all()
    )

def get_all_adoptions(db: Session):
    return (
        db.query(Adoption)
        .options(joinedload(Adoption.pet), joinedload(Adoption.user))
        .all()
    )

def update_adoption_status(db: Session, adoption_id: int, status: str):
    adoption = db.query(Adoption).filter(Adoption.id == adoption_id).first()
    if adoption:
        adoption.status = status
        db.commit()
        db.refresh(adoption)
    return adoption

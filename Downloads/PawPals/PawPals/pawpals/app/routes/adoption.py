from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.schemas.adoption import AdoptionCreate, AdoptionOut, AdoptionUpdate
from app.crud import adoption as crud_adoption
from app.database import get_db
from app.deps import get_current_user, get_current_admin
from app.models.adoption import Adoption  # ✅ tambahkan ini

router = APIRouter()


@router.post("/", response_model=AdoptionOut)
def ajukan_adopsi(payload: AdoptionCreate, db: Session = Depends(get_db), user = Depends(get_current_user)):
    return crud_adoption.create_adoption(db, user_id=user.id, pet_id=payload.pet_id)

@router.get("/me", response_model=list[AdoptionOut])
def lihat_pengajuan_saya(db: Session = Depends(get_db), user = Depends(get_current_user)):
    return crud_adoption.get_user_adoptions(db, user_id=user.id)

@router.get("/", response_model=list[AdoptionOut])
def semua_pengajuan(db: Session = Depends(get_db), _: str = Depends(get_current_admin)):
    return crud_adoption.get_all_adoptions(db)

@router.put("/{adoption_id}", response_model=AdoptionOut)
def update_status(adoption_id: int, payload: AdoptionUpdate, db: Session = Depends(get_db), _: str = Depends(get_current_admin)):
    result = crud_adoption.update_adoption_status(db, adoption_id, status=payload.status)
    if not result:
        raise HTTPException(status_code=404, detail="Data adopsi ga ketemu")
    return result

@router.delete("/{adoption_id}")
def delete_adoption(adoption_id: int, db: Session = Depends(get_db), _: str = Depends(get_current_admin)):
    adoption = db.query(Adoption).filter(Adoption.id == adoption_id).first()
    if not adoption:
        raise HTTPException(status_code=404, detail="Data adopsi tidak ditemukan")
    db.delete(adoption)
    db.commit()
    return {"msg": "Pengajuan adopsi berhasil dihapus"}

@router.delete("/me/{adoption_id}")
def delete_my_adoption(adoption_id: int, db: Session = Depends(get_db), user = Depends(get_current_user)):
    adoption = db.query(Adoption).filter(Adoption.id == adoption_id, Adoption.user_id == user.id).first()
    if not adoption:
        raise HTTPException(status_code=404, detail="Data adopsi tidak ditemukan atau bukan milik kamu")
    db.delete(adoption)
    db.commit()
    return {"msg": "Pengajuan adopsi kamu berhasil dibatalkan"}


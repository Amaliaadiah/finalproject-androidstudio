from fastapi import APIRouter, Depends, HTTPException, UploadFile, File, Form
from sqlalchemy.orm import Session
from app.schemas.pet import PetOut
from app.crud import pet as crud_pet
from app.database import get_db
from app.deps import get_current_admin, get_current_user
import shutil
import os
import uuid

router = APIRouter()

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)  # bikin folder upload kalau belum ada

# 🔥 CREATE PET
@router.post("/", response_model=PetOut)
def create_pet(
    name: str = Form(...),
    type: str = Form(...),
    age: int = Form(...),
    description: str = Form(...),
    image: UploadFile = File(None),
    db: Session = Depends(get_db),
    _: str = Depends(get_current_admin)
):
    image_url = None

    if image:
        ext = image.filename.split(".")[-1]
        filename = f"{uuid.uuid4()}.{ext}"
        file_path = os.path.join(UPLOAD_DIR, filename)
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(image.file, buffer)
        image_url = f"/{file_path}"

    pet_data = {
        "name": name,
        "type": type,
        "age": age,
        "description": description,
        "image_url": image_url
    }

    return crud_pet.create_pet(db, pet_data)

# 🔥 GET ALL PETS
@router.get("/", response_model=list[PetOut])
def get_all_pets(db: Session = Depends(get_db), _: str = Depends(get_current_user)):
    return crud_pet.get_pets(db)

# 🔥 GET SINGLE PET
@router.get("/{pet_id}", response_model=PetOut)
def get_pet(pet_id: int, db: Session = Depends(get_db), _: str = Depends(get_current_user)):
    pet = crud_pet.get_pet_by_id(db, pet_id)
    if not pet:
        raise HTTPException(status_code=404, detail="Pet tidak ditemukan")
    return pet

# 🔥 UPDATE PET
@router.put("/{pet_id}", response_model=PetOut)
def update_pet(
    pet_id: int,
    name: str = Form(...),
    type: str = Form(...),
    age: int = Form(...),
    description: str = Form(...),
    image: UploadFile = File(None),
    db: Session = Depends(get_db),
    _: str = Depends(get_current_admin)
):
    pet = crud_pet.get_pet_by_id(db, pet_id)
    if not pet:
        raise HTTPException(status_code=404, detail="Pet tidak ditemukan")

    image_url = pet.image_url
    if image:
        ext = image.filename.split(".")[-1]
        filename = f"{uuid.uuid4()}.{ext}"
        file_path = os.path.join(UPLOAD_DIR, filename)
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(image.file, buffer)
        image_url = f"/{file_path}"

    pet_data = {
        "name": name,
        "type": type,
        "age": age,
        "description": description,
        "image_url": image_url
    }

    return crud_pet.update_pet(db, pet_id, pet_data)

# 🔥 DELETE PET
@router.delete("/{pet_id}")
def delete_pet(pet_id: int, db: Session = Depends(get_db), _: str = Depends(get_current_admin)):
    deleted = crud_pet.delete_pet(db, pet_id)
    if not deleted:
        raise HTTPException(status_code=404, detail="Pet tidak ditemukan")
    return {"msg": "Pet berhasil dihapus"}

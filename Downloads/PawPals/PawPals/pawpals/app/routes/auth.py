from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.schemas.user import UserCreate, UserOut, UserLogin, Token
from app.crud import user as crud_user
from app.database import SessionLocal
from app.core.security import verify_password, create_access_token

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# ✅ REGISTER
@router.post("/register", response_model=UserOut)
def register(user_in: UserCreate, db: Session = Depends(get_db)):
    db_user = crud_user.get_user_by_username(db, user_in.username)
    if db_user:
        raise HTTPException(status_code=400, detail="Username udah dipake cuy")
    return crud_user.create_user(db, user_in)

# ✅ LOGIN
@router.post("/login", response_model=Token)
def login(user_in: UserLogin, db: Session = Depends(get_db)):
    user = crud_user.get_user_by_username(db, user_in.username)
    if not user or not verify_password(user_in.password, user.hashed_password):
        raise HTTPException(status_code=401, detail="Login gagal bro")

    # ✅ Tambahin role ke token
    token = create_access_token({
        "sub": str(user.id),
        "username": user.username,
        "role": user.role
    })
    return {"access_token": token, "token_type": "bearer"}

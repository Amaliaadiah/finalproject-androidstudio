from sqlalchemy.orm import Session
from app.models.user import User
from app.schemas.user import UserCreate
from app.core.security import hash_password

def get_user_by_username(db: Session, username: str):
    return db.query(User).filter(User.username == username).first()

def create_user(db: Session, user: UserCreate):
    hashed = hash_password(user.password)
    new_user = User(
        username=user.username,
        hashed_password=hashed,
        role=user.role or "user" 
    )
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    return new_user

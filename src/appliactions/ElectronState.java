package appliactions;

interface ElectronState {
	boolean isGround();
}

class Ground implements ElectronState {
	@Override
	public boolean isGround() {
		return true;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

class Excited implements ElectronState {
	@Override
	public boolean isGround() {
		return false;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
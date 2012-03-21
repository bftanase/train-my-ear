package ro.btanase.chordlearning.frames;

public interface IDialog<E> {
  public void onSubmit(E object);
  public void onCancel(E object);
}

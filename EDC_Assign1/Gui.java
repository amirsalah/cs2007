public interface Gui
{
    //Connect gui to controller
    //(This method will be called before any other methods)
    public void connect(Controller controller);

    //Initialise the gui
    public void init();

    //Called to change the displayed text
    public void setDisplay(String s);
}

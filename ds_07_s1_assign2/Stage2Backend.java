public interface Stage2Backend extends Stage1Backend {
  public String[] getTranscriptList();
  public void selectTranscript(String transcriptName);
  public void shutdownAndSave(String transcriptName);
  public void shutdownAndAbort();  
  public void attach(Stage2UserInterface ui);
}
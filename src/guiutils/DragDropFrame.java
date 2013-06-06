package guiutils;

import genesis.GenesisApp;

import java.awt.dnd.DropTarget;

import javax.swing.JFrame;

public class DragDropFrame extends JFrame {

private static final long serialVersionUID = 1L;
GenesisApp app;

public DragDropFrame(GenesisApp a) {

    super("Drag and drop test");
    app = a;

    // Create the drag and drop listener
    DragDropListener myDragDropListener = new DragDropListener(app);

    // Connect the label with a drag and drop listener
    new DropTarget(this, myDragDropListener);

  
}

}
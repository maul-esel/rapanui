## 6 UI Implementation

The UI is implemented using a modified (ModelView - ViewModel) MVVM pattern.

`rapanui.ui.models` contains the view models which encapsulate the core classes and provide additional display data as well as customized interaction logic. They communicate with the views not directly, but through `Observer` interfaces which the views implement, and by modifying AWT/Swing data models.

The views in `rapanui.ui.views` are created with a reference to their view model. They create the actual user interface, using both standard Swing and customized controls (`rapanui.ui.controls`) around the Swing data models the view model exposes.

To encapsulate an action, common display data for the action, and a check for its executability, the command pattern is implemented in `rapanui.ui.commands`.

When the user performs an action on the user interface, the corresponding command is executed. It delegates to the view model, which modifies the views and delegates calls to the core class instances.

When a core class is modified, it notifies its observers (including the encapsulating view model), which reacts by modifying the view through the mentioned channels.

# Shopper
A Shopping list app.

## Basic Features

### Shopping List View
The main view is a list of the user's shopping lists:
![user's shopping lists](readme_images/shopping_lists_view.png)

The user can select any of these lists to view the items in them.

The user can select `+` to add shopping lists:
![add new shopping list](readme_images/add_shopping_list_dialog.png)

From the extended menu (3 vertical dots) to delete all shopping lists and associated items:
![delete all shopping lists and associated items](readme_images/shopping_lists_view_extended_menu.png)

### Shopping List Items View
After selecting a shopping list, the user is presented with a list of items associated with that list:
![shopping list items associated with a shopping list](readme_images/shopping_list_items_view.png)

Users can add items or sections. Items can be added directly to sections.

Users can mark an item as complete (e.g., retrieved from store/shelf) or incomplete (e.g., not yet retrieved from store/shelf). Complete items have the text crossed out, and incomplete items are bold.

Users can add new items and sections:
![adding new items and sections](readme_images/add_shopping_list_item_view.png)

Users can also edit existing items and sections:
![edit existing items and sections](readme_images/update_item_view.png)

From the extended menu, the user can also delete all items in a shopping list, mark all items as complete, or mark all items as incomplete:
![shopping list items view extended menu](readme_images/shopping_list_items_extended_menu.png)

## Testing
### Common Errors
* Animations will impact tests. [Follow the instructions here](https://developer.android.com/studio/debug/dev-options.html) to disable animations on the testing device.

# ARCHIVAL NOTICE
This project is being rebuilt and modernized in a new project called [TODO](https://github.com/johneastman/todo). This repository has been archived. 

# Shopper
A Shopping list app.

## Basic Features

### Shopping List View
The main view is a list of the user's shopping lists:

<img src="readme_images/shopping_lists_view.png" alt="user's shopping lists" width="342.5" height="633">

The user can select any of these lists to view the items in those lists.

The user can select `+` to create a shopping list:

<img src="readme_images/add_shopping_list_dialog.png" alt="add new shopping list" width="342.5" height="633">

From the extended menu (`⋮`), the user can choose to delete all shopping lists and associated items:

<img src="readme_images/shopping_lists_view_extended_menu.png" alt="delete all shopping lists and associated items" width="342.5" height="633">

### Shopping List Items View
After selecting a shopping list, the user is presented with a list of items associated with that list:

<img src="readme_images/shopping_list_items_view.png" alt="shopping list items associated with a shopping list" width="342.5" height="633">

Users can add items or sections to the shopping list by selecting `+`. Items can also be added directly to sections (via the `+` on the section).

Users can mark items as complete (e.g., retrieved from store/shelf) or incomplete (e.g., not yet retrieved from store/shelf). Complete items have the text crossed out, and incomplete items are bold.

Add item/section dialog:

<img src="readme_images/add_shopping_list_item_view.png" alt="adding new items and sections" width="342.5" height="633">

Users can also edit existing items and sections by selecting the pencil icon:

<img src="readme_images/update_item_view.png" alt="edit existing items and sections" width="342.5" height="633">

From the extended menu (`⋮`), users can delete all items in a shopping list, mark all items as complete, or mark all items as incomplete:

<img src="readme_images/shopping_list_items_extended_menu.png" alt="shopping list items view extended menu" width="342.5" height="633">

## Testing

### Common Errors
* Animations will impact tests. [Follow the instructions here](https://developer.android.com/studio/debug/dev-options.html) to disable animations on the testing device.

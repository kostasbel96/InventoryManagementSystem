import { Component } from '@angular/core';
import { MenuEntry } from 'src/app/shared/interfaces/menu-entry';

@Component({
  selector: 'app-list-group-menu',
  templateUrl: './list-group-menu.component.html',
  styleUrls: ['./list-group-menu.component.css']
})
export class ListGroupMenuComponent {

  menu: MenuEntry[] = [
    { text: "Products", routerLink: "products" },
    { text: "Orders", routerLink:"orders"},
    { text: "Suppliers", routerLink:"suppliers"},
    { text: "Categories", routerLink:"categories"}
  ]

}

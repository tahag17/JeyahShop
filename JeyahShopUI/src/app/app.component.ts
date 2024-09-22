import { Component } from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {MenuComponent} from "./pages/menu/menu.component";
import {FooterComponent} from "./pages/footer/footer.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    MenuComponent,
    FooterComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'JeyahShopUI';
}

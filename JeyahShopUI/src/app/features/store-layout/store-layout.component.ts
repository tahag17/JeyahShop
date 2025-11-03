import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'app-store-layout',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './store-layout.component.html',
  styleUrl: './store-layout.component.scss',
})
export class StoreLayoutComponent {}

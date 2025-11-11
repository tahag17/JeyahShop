import { Component, OnInit } from '@angular/core';
import { SimpleProductResponse } from '../../shared/models/simple-product-response';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule], // <-- CommonModule includes keyvalue pipe
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  topRated: SimpleProductResponse[] = [];
  latest: SimpleProductResponse[] = [];
  categorySamples: Record<string, SimpleProductResponse[]> = {};

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any>(`${environment.apiBaseUrl}public/api/home`).subscribe({
      next: (data) => {
        this.topRated = data.topRated;
        this.latest = data.latest;
        this.categorySamples = data.categorySamples;
      },
      error: (err) => console.error(err),
    });
  }
}

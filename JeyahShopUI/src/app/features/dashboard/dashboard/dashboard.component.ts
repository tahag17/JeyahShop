import { HttpClient } from '@angular/common/http';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../environments/environment';
import { CommonModule, NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgFor, NgIf, CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit, OnDestroy {
  period: 'day' | 'month' | 'year' = 'day';
  data: any = {};
  loading = false;
  private chartInstance: any = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadData(this.period);
  }

  ngOnDestroy() {
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  }

  loadData(period: 'day' | 'month' | 'year') {
    this.period = period;
    this.loading = true;

    this.http
      .get<any>(`${environment.apiBaseUrl}admin/api/dashboard/${period}`, {
        withCredentials: true,
      })
      .subscribe({
        next: (res) => {
          console.log('✅ Dashboard Data Response:', res);
          this.data = res;
          this.loading = false;
          setTimeout(() => this.renderCategoryChart(), 50);
        },
        error: (err) => {
          console.error('❌ Dashboard Data Error:', err);
          this.loading = false;
        },
      });
  }

  renderCategoryChart() {
    if (!this.data.topCategories) return;

    import('chart.js').then(
      ({ Chart, ArcElement, Tooltip, Legend, DoughnutController }) => {
        Chart.register(DoughnutController, ArcElement, Tooltip, Legend);

        const ctx = document.getElementById(
          'categoryChart'
        ) as HTMLCanvasElement;
        if (!ctx) return;

        if (this.chartInstance) {
          this.chartInstance.destroy();
        }

        this.chartInstance = new Chart(ctx, {
          type: 'doughnut',
          data: {
            labels: this.data.topCategories.map((c: any) => c.name),
            datasets: [
              {
                data: this.data.topCategories.map((c: any) => c.totalSold),
                backgroundColor: [
                  '#b83232',
                  '#2541B2',
                  '#03256C',
                  '#ba0808',
                  '#e0a64f',
                  '#781c21',
                  '#5c0f15',
                  '#b83c3c',
                  '#ffab76',
                  '#ff6347',
                ],
              },
            ],
          },
          options: {
            maintainAspectRatio: false,
            responsive: true,
            plugins: {
              legend: {
                position: 'bottom',
                labels: { boxWidth: 12, padding: 10 },
              },
            },
            layout: { padding: 10 },
          },
        });
      }
    );
  }

  getStarClass(rate: number | string | undefined, starIndex: number): string {
    const r = Number(rate ?? 0);
    if (r >= starIndex) return 'fa-solid fa-star';
    if (r >= starIndex - 0.5) return 'fa-solid fa-star-half-stroke';
    return 'fa-regular fa-star';
  }

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    console.warn('[Image Error] Original src:', img.src);
    img.style.display = 'none'; // hide broken image
  }
}

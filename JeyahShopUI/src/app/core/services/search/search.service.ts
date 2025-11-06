import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private searchKeyword$ = new BehaviorSubject<string>('');
  keyword$ = this.searchKeyword$.asObservable();

  setKeyword(keyword: string) {
    this.searchKeyword$.next(keyword);
  }

  getCurrentKeyword(): string {
    return this.searchKeyword$.value;
  }
}

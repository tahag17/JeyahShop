import { NgModule } from '@angular/core';
import { HlmIcon } from './lib/hlm-icon';

export * from './lib/hlm-icon';
export * from './lib/hlm-icon.token';

@NgModule({
  declarations: [HlmIcon], // declare the directive
  exports: [HlmIcon], // export it so other modules can use it
})
export class HlmIconModule {}

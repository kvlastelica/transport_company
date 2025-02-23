import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IRoute } from '../route.model';

@Component({
  selector: 'jhi-route-detail',
  templateUrl: './route-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class RouteDetailComponent {
  route = input<IRoute | null>(null);

  previousState(): void {
    window.history.back();
  }
}

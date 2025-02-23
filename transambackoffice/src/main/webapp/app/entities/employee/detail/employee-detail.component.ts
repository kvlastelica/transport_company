import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IEmployee } from '../employee.model';

@Component({
  selector: 'jhi-employee-detail',
  templateUrl: './employee-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class EmployeeDetailComponent {
  employee = input<IEmployee | null>(null);

  previousState(): void {
    window.history.back();
  }
}

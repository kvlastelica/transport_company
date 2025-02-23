import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRouteSchedule, NewRouteSchedule } from '../route-schedule.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRouteSchedule for edit and NewRouteScheduleFormGroupInput for create.
 */
type RouteScheduleFormGroupInput = IRouteSchedule | PartialWithRequiredKeyOf<NewRouteSchedule>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRouteSchedule | NewRouteSchedule> = Omit<T, 'departure' | 'arrival'> & {
  departure?: string | null;
  arrival?: string | null;
};

type RouteScheduleFormRawValue = FormValueOf<IRouteSchedule>;

type NewRouteScheduleFormRawValue = FormValueOf<NewRouteSchedule>;

type RouteScheduleFormDefaults = Pick<NewRouteSchedule, 'id' | 'departure' | 'arrival'>;

type RouteScheduleFormGroupContent = {
  id: FormControl<RouteScheduleFormRawValue['id'] | NewRouteSchedule['id']>;
  code: FormControl<RouteScheduleFormRawValue['code']>;
  description: FormControl<RouteScheduleFormRawValue['description']>;
  departure: FormControl<RouteScheduleFormRawValue['departure']>;
  arrival: FormControl<RouteScheduleFormRawValue['arrival']>;
  route: FormControl<RouteScheduleFormRawValue['route']>;
};

export type RouteScheduleFormGroup = FormGroup<RouteScheduleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RouteScheduleFormService {
  createRouteScheduleFormGroup(routeSchedule: RouteScheduleFormGroupInput = { id: null }): RouteScheduleFormGroup {
    const routeScheduleRawValue = this.convertRouteScheduleToRouteScheduleRawValue({
      ...this.getFormDefaults(),
      ...routeSchedule,
    });
    return new FormGroup<RouteScheduleFormGroupContent>({
      id: new FormControl(
        { value: routeScheduleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(routeScheduleRawValue.code, {
        validators: [Validators.required],
      }),
      description: new FormControl(routeScheduleRawValue.description, {
        validators: [Validators.required],
      }),
      departure: new FormControl(routeScheduleRawValue.departure, {
        validators: [Validators.required],
      }),
      arrival: new FormControl(routeScheduleRawValue.arrival, {
        validators: [Validators.required],
      }),
      route: new FormControl(routeScheduleRawValue.route, {
        validators: [Validators.required],
      }),
    });
  }

  getRouteSchedule(form: RouteScheduleFormGroup): IRouteSchedule | NewRouteSchedule {
    return this.convertRouteScheduleRawValueToRouteSchedule(form.getRawValue() as RouteScheduleFormRawValue | NewRouteScheduleFormRawValue);
  }

  resetForm(form: RouteScheduleFormGroup, routeSchedule: RouteScheduleFormGroupInput): void {
    const routeScheduleRawValue = this.convertRouteScheduleToRouteScheduleRawValue({ ...this.getFormDefaults(), ...routeSchedule });
    form.reset(
      {
        ...routeScheduleRawValue,
        id: { value: routeScheduleRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RouteScheduleFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      departure: currentTime,
      arrival: currentTime,
    };
  }

  private convertRouteScheduleRawValueToRouteSchedule(
    rawRouteSchedule: RouteScheduleFormRawValue | NewRouteScheduleFormRawValue,
  ): IRouteSchedule | NewRouteSchedule {
    return {
      ...rawRouteSchedule,
      departure: dayjs(rawRouteSchedule.departure, DATE_TIME_FORMAT),
      arrival: dayjs(rawRouteSchedule.arrival, DATE_TIME_FORMAT),
    };
  }

  private convertRouteScheduleToRouteScheduleRawValue(
    routeSchedule: IRouteSchedule | (Partial<NewRouteSchedule> & RouteScheduleFormDefaults),
  ): RouteScheduleFormRawValue | PartialWithRequiredKeyOf<NewRouteScheduleFormRawValue> {
    return {
      ...routeSchedule,
      departure: routeSchedule.departure ? routeSchedule.departure.format(DATE_TIME_FORMAT) : undefined,
      arrival: routeSchedule.arrival ? routeSchedule.arrival.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}

import { convertToParamMap, Params, ActivatedRouteSnapshot } from '@angular/router';
import { ReplaySubject } from 'rxjs';

export class ActivatedRouteStub {
    private subject = new ActivatedRouteSnapshot();

    constructor(initialParams?: Params) {
        this.setParamMap(initialParams);
    }

    /** The mock paramMap observable */
    readonly snapshot = this.subject;

    /** Set the paramMap observables's next value */
    setParamMap(params?: Params) {
        this.subject.params = params;
    }
}
